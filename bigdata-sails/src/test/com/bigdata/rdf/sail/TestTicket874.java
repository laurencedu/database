/**
Copyright (C) SYSTAP, LLC 2011.  All rights reserved.

Contact:
     SYSTAP, LLC
     4501 Tower Road
     Greensboro, NC 27410
     licenses@bigdata.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.bigdata.rdf.sail;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.BindingImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailTupleQuery;
import org.openrdf.rio.RDFFormat;

import com.bigdata.rdf.axioms.NoAxioms;
import com.bigdata.rdf.vocab.NoVocabulary;

/**
 * Unit test template for use in submission of bugs.
 * <p>
 * This test case will delegate to an underlying backing store.  You can
 * specify this store via a JVM property as follows:
 * <code>-DtestClass=com.bigdata.rdf.sail.TestBigdataSailWithQuads</code>
 * <p>
 * There are three possible configurations for the testClass:
 * <ul>
 * <li>com.bigdata.rdf.sail.TestBigdataSailWithQuads (quads mode)</li>
 * <li>com.bigdata.rdf.sail.TestBigdataSailWithoutSids (triples mode)</li>
 * <li>com.bigdata.rdf.sail.TestBigdataSailWithSids (SIDs mode)</li>
 * </ul>
 * <p>
 * The default for triples and SIDs mode is for inference with truth maintenance
 * to be on.  If you would like to turn off inference, make sure to do so in
 * {@link #getProperties()}.
 *  
 * @author <a href="mailto:mrpersonick@users.sourceforge.net">Mike Personick</a>
 * @version $Id$
 */
public class TestTicket874 extends ProxyBigdataSailTestCase {

    protected static final Logger log = Logger.getLogger(TestTicket874.class);

    /**
     * Please set your database properties here, except for your journal file,
     * please DO NOT SPECIFY A JOURNAL FILE. 
     */
    @Override
    public Properties getProperties() {
        
        Properties props = super.getProperties();

        /*
         * For example, here is a set of five properties that turns off
         * inference, truth maintenance, and the free text index.
         */
        props.setProperty(BigdataSail.Options.AXIOMS_CLASS, NoAxioms.class.getName());
        props.setProperty(BigdataSail.Options.VOCABULARY_CLASS, NoVocabulary.class.getName());
        props.setProperty(BigdataSail.Options.TRUTH_MAINTENANCE, "false");
        props.setProperty(BigdataSail.Options.JUSTIFY, "false");
        props.setProperty(BigdataSail.Options.TEXT_INDEX, "false");
        
        return props;
        
    }

    public TestTicket874() {
    }

    public TestTicket874(String arg0) {
        super(arg0);
    }
    
    public void testBug1() throws Exception {
    	
        /*
         * The bigdata store, backed by a temporary journal file.
         */
	  	final BigdataSail bigdataSail = getSail();
	  	
	  	/*
	  	 * Data file containing the data demonstrating your bug.
	  	 */
	  	final String data = "874.ttl";
	  	final String baseURI = "";
	  	final RDFFormat format = RDFFormat.TURTLE;
	  	
	  	try {
	  	
	  		bigdataSail.initialize();
	  		
  			final BigdataSailRepository bigdataRepo = new BigdataSailRepository(bigdataSail);
  			
	  		{ // load the data into the bigdata store
	  			
	  			final RepositoryConnection cxn = bigdataRepo.getConnection();
	  			try {
	  				cxn.setAutoCommit(false);
	  				cxn.add(getClass().getResourceAsStream(data), baseURI, format);
//	  				cxn.add(data);
	  				cxn.commit();
	  			} finally {
	  				cxn.close();
	  			}
	  			
	  		}

	  		{
//	  			final Collection<BindingSet> answer = new LinkedList<BindingSet>();
//	            answer.add(createBindingSet(
//	            		new BindingImpl("sub", new URIImpl("http://example.org/B"))
//	            		));

	            final String query = IOUtils.toString(getClass().getResourceAsStream("874.rq"));
	    	  	
	            if (log.isInfoEnabled()) {
	            	log.info("running query:\n" + query);
	            }
            
	            /*
	             * Run the problem query using the bigdata store and then compare
	             * the answer.
	             */
	            final RepositoryConnection cxn = bigdataRepo.getReadOnlyConnection();
	  			try {
	  				
		            final SailTupleQuery tupleQuery = (SailTupleQuery)
		                cxn.prepareTupleQuery(QueryLanguage.SPARQL, query);
		            tupleQuery.setIncludeInferred(false /* includeInferred */);
	            	
		            final TupleQueryResult result = tupleQuery.evaluate();
//	            	compare(result, answer);
		            
		            while (result.hasNext()) {
		            	log.info(result.next());
		            }
	            	
	  			} finally {
	  				cxn.close();
	  			}
	  			
	  		}
          
        } finally {
        	
        	bigdataSail.__tearDownUnitTest();
        	
        }
    	
    }

}
