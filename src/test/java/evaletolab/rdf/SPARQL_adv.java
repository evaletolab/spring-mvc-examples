package evaletolab.rdf;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;

import evaletolab.config.WebConfig;

/**
 * Use case for evidences queries
 * - Q53	which are involved in cell adhesion according to GO with an evidence not IAE and not ISS
 * - Q57	which are located in mitochondrion with an evidence other than HPA and DKFZ-GFP
 * - Q63	which have >=1 RRM RNA-binding domain and either no GO "RNA binding" other a GO "RNA binding" with evidence IEA or ISS
 * - Q68	with protein evidence PE=2 (transcript level)
 *  
 * @author evaleto
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class SPARQL_adv {
	
	Model m, schema;
	InfModel rdfs;
	
	@Before
	public void setup() {
		m=ModelFactory.createDefaultModel();
		schema=ModelFactory.createDefaultModel();
		m.read("evidence.ttl").read("terminology-disease.ttl").read("publication.ttl");
        schema = schema.read("owl/np.ttl");

//        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
//        reasoner = reasoner.bindSchema(schema);
//        rdfs = ModelFactory.createInfModel(reasoner, m);
        
		rdfs= ModelFactory.createRDFSModel(schema, m);


	}
	
	/**
	 * PREPARE QUERY Q53
	 * 	which are involved in cell adhesion according to GO  
	 *      - Cell adhesion [GO:0007155] 
	 */
	@Test
	public void involvedInGO0007155(){
		// query
		String q="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				 "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
				 "PREFIX : <http://nextprot.org/rdf#> " +
				 "PREFIX term: <http://nextprot.org/rdf/terminology/> " +
				 "SELECT ?involvedInGO0007155 WHERE { " +
				 "  ?involvedInGO0007155 :isoform/:function/:in term:GO:0007155"+
				 "}";	
		Query query = QueryFactory.create(q);
        QueryExecution qe = QueryExecutionFactory.create(query,rdfs);
                
        
        ResultSetFormatter.out(System.out, qe.execSelect(), query);
	}	
	
	/**
	 *  UNION	
	 * 	with an evidence IAE or ISS   
	 */
	@Test
	public void union_sparql_union(){
		// query
		String q="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				 "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
				 "PREFIX : <http://nextprot.org/rdf#> " +
				 "PREFIX term: <http://nextprot.org/rdf/terminology/> " +
				 "PREFIX  list: <http://jena.hpl.hp.com/ARQ/list#> "+
				 "SELECT * WHERE { " +
				 "  {?union_sparql_union :isoform/:function/:evidence/rdf:type :IAE}"+
				 "UNION "+
				 "  {?union_sparql_union :isoform/:function/:evidence/rdf:type :ISS}"+
				 "}";	
		
		Query query = QueryFactory.create(q);
        QueryExecution qe = QueryExecutionFactory.create(query,rdfs);
                
        
        ResultSetFormatter.out(System.out, qe.execSelect(), query);
	}		

	/**
	 * UNION by FILTER	
	 * 	with an evidence IAE or ISS   
	 *  -- FILTER HAS ALWAYS BAD PERFORMANCE --
	 */
	@Test
	public void union_by_filter(){
		// query
		String q="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				 "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
				 "PREFIX : <http://nextprot.org/rdf#> " +
				 "PREFIX term: <http://nextprot.org/rdf/terminology/> " +
				 "PREFIX  list: <http://jena.hpl.hp.com/ARQ/list#> "+
				 "SELECT * WHERE { " +
				 "  ?union_by_filter :isoform/:function/:evidence/rdf:type ?eco"+
				 "  FILTER ( ?eco = :IAE || ?eco = :ISS )"+
				 "}";	
		
		Query query = QueryFactory.create(q);
        QueryExecution qe = QueryExecutionFactory.create(query,rdfs);
                
        ResultSetFormatter.out(System.out, qe.execSelect(), query);
	}		
	
	/**
	 * UNION by unionOf 	
	 * 	with an evidence IAE or ISS   
	 */
	@Test
	public void union_unionOf(){
		// query
		String q="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				 "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
				 "PREFIX : <http://nextprot.org/rdf#> " +
				 "PREFIX term: <http://nextprot.org/rdf/terminology/> " +
				 "PREFIX  list: <http://jena.hpl.hp.com/ARQ/list#> "+
				 "SELECT * WHERE { " +
				 "  ?union_unionOf owl:unionOf(:ISS :IAE)"+
				 "}";	
		
		Query query = QueryFactory.create(q);
        QueryExecution qe = QueryExecutionFactory.create(query,rdfs);                
        ResultSetFormatter.out(System.out, qe.execSelect(), query);
	}		
	

	/**
	 * NOT IN by owl:differentFrom 	
	 */
	@Test
	public void notInByDifferentFrom(){
		// query
		String q="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				 "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
				 "PREFIX : <http://nextprot.org/rdf#> " +
				 "PREFIX term: <http://nextprot.org/rdf/terminology/> " +
				 "SELECT * WHERE { " +
				 "  ?notInByDifferentFrom :evidence/rdf:type ?t." +
				 "  ?t owl:differentFrom :IAE"+
				 "}";	
		Query query = QueryFactory.create(q);
        QueryExecution qe = QueryExecutionFactory.create(query,rdfs);
        ResultSetFormatter.out(System.out, qe.execSelect(), query);
	}	

	/**
	 * NOT IN by owl:complementOf 	
	 */
	@Test
	public void notInByComplementOf(){
		// query
		String q="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				 "PREFIX owl: <http://www.w3.org/2002/07/owl#> "+
				 "PREFIX : <http://nextprot.org/rdf#> " +
				 "PREFIX term: <http://nextprot.org/rdf/terminology/> " +
				 "SELECT * WHERE { " +
				 "  ?notInByComplementOf :evidence/rdfs:type ?t." +
				 "  ?t owl:complementOf :IAE"+
				 "}";	
		Query query = QueryFactory.create(q);
        QueryExecution qe = QueryExecutionFactory.create(query,rdfs);
        ResultSetFormatter.out(System.out, qe.execSelect(), query);
	}	

}