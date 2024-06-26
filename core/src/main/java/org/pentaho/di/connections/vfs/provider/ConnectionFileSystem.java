/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2019-2024 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.connections.vfs.provider;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.pentaho.di.connections.ConnectionDetails;
import org.pentaho.di.connections.ConnectionManager;
import org.pentaho.di.connections.vfs.VFSConnectionDetails;
import org.pentaho.di.core.bowl.Bowl;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.core.vfs.configuration.IKettleFileSystemConfigBuilder;
import org.pentaho.di.core.vfs.configuration.KettleFileSystemConfigBuilderFactory;
import org.pentaho.di.core.vfs.configuration.KettleGenericFileSystemConfigBuilder;

import java.util.Collection;

public class ConnectionFileSystem extends AbstractFileSystem implements FileSystem {

  public static final String CONNECTION = "connection";
  public static final String DOMAIN_ROOT = "[\\w]+://";

  public ConnectionFileSystem( FileName rootName, FileSystemOptions fileSystemOptions ) {
    super( rootName, null, fileSystemOptions );
  }

  /**
   * Creates a url for {@link ConnectionFileName}
   *
   * @param abstractFileName  File name
   * @param connectionDetails Connection details for the file name
   * @return created url otherwise null
   */
  public static String getUrl( AbstractFileName abstractFileName, ConnectionDetails connectionDetails ) {
    VFSConnectionDetails vfsConnectionDetails = (VFSConnectionDetails) connectionDetails;
    String url = null;

    if ( vfsConnectionDetails != null ) {
      String domain = vfsConnectionDetails.getDomain();
      if ( !domain.equals( "" ) ) {
        domain = "/" + domain;
      }
      url = vfsConnectionDetails.getType() + ":/" + domain + abstractFileName.getPath();
      //TODO Looks like a bug. For now excluding this for connections with hasBuckets. For future, needs to be re-analyzed.
      if ( url.matches( DOMAIN_ROOT ) && vfsConnectionDetails.hasBuckets() ) {
        url += vfsConnectionDetails.getName();
      }
    }

    return url;
  }

  @Override
  protected FileObject createFile( AbstractFileName abstractFileName ) throws Exception {

    String connectionName = ( (ConnectionFileName) abstractFileName ).getConnection();
    Bowl bowl = KettleGenericFileSystemConfigBuilder.getInstance().getBowl( getFileSystemOptions() );
    VFSConnectionDetails connectionDetails =
      (VFSConnectionDetails) bowl.getConnectionManager().getConnectionDetails( connectionName );
    FileSystemOptions opts = super.getFileSystemOptions();
    IKettleFileSystemConfigBuilder configBuilder = KettleFileSystemConfigBuilderFactory.getConfigBuilder
      ( new Variables(), ConnectionFileProvider.SCHEME );
    VariableSpace varSpace = (VariableSpace) configBuilder.getVariableSpace( super.getFileSystemOptions() );
    if ( connectionDetails != null ) {
      connectionDetails.setSpace( varSpace );
    }
    String url = getUrl( abstractFileName, connectionDetails );

    AbstractFileObject fileObject = null;
    String domain = null;

    if ( url != null ) {
      domain = connectionDetails.getDomain();
      varSpace.setVariable( CONNECTION, connectionName );
      fileObject = (AbstractFileObject) KettleVFS.getInstance( bowl ).getFileObject( url, varSpace );
    }

    return new ConnectionFileObject( abstractFileName, this, fileObject, domain );
  }

  @Override protected void addCapabilities( Collection<Capability> collection ) {
    collection.addAll( ConnectionFileProvider.capabilities );
  }

  @Override
  public FileObject resolveFile( FileName name ) throws FileSystemException {
    try {
      return this.createFile( (AbstractFileName) name );
    } catch ( Exception e ) {
      throw new FileSystemException( "vfs.provider/resolve-file.error", name, e );
    }
  }

}
