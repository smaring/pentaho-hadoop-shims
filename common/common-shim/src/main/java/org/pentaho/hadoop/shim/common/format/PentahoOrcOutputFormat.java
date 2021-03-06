/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.hadoop.shim.common.format;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;
import org.apache.orc.TypeDescription;
import org.pentaho.hadoop.shim.api.format.IPentahoOrcOutputFormat;
import org.pentaho.hadoop.shim.api.format.SchemaDescription;

import org.pentaho.hadoop.shim.common.ConfigurationProxy;
import org.pentaho.hadoop.shim.common.format.orc.OrcSchemaConverter;
import org.pentaho.hadoop.shim.common.format.orc.PentahoOrcRecordWriter;

/**
 * Created by tkafalas on 11/3/2017.
 */
public class PentahoOrcOutputFormat extends HadoopFormatBase implements IPentahoOrcOutputFormat {
  private TypeDescription schema;
  private String outputFilename;
  private SchemaDescription schemaDescription;
  private Configuration conf;
  private COMPRESSION compression = COMPRESSION.NONE;

  private static final Logger logger = Logger.getLogger( PentahoOrcOutputFormat.class );

  public PentahoOrcOutputFormat() throws Exception {
    conf = inClassloader( () -> {
      Configuration conf = new ConfigurationProxy();
      conf.addResource( "hive-site.xml" );
      return conf;
    } );
  }

  @Override public IPentahoRecordWriter createRecordWriter() throws Exception {
    logger.info( "Initializing Orc Writer" );
    if ( schemaDescription == null ) {
      throw new Exception( "Invalid state.  The schemaDescription is null" );
    }
    if ( outputFilename == null ) {
      throw new Exception( "Invalid state.  The outputFileName is null" );
    }
    OrcSchemaConverter converter = new OrcSchemaConverter( );
    schema = converter.buildTypeDescription( schemaDescription );

    return new PentahoOrcRecordWriter( schemaDescription, schema, outputFilename, conf );
  }

  @Override public void setSchemaDescription( SchemaDescription schema ) throws Exception {
    schemaDescription = schema;
  }

  @Override public void setOutputFile( String file ) throws Exception {
    outputFilename = file;
  }

  @Override public void setCompression( COMPRESSION compression ) {
    this.compression = compression;
    conf.set( "hive.exec.orc.default.compress", compression.toString() );
  }


}
