import org.junit.Assert;
import java.util.Arrays;
import java.util.Collections;
import java.util.*;

import org.junit.Before;
import org.junit.Test;

public class AutocompleteTest {

   public Autocomplete.Autocompletor n;

   /** Fixture initialization (common initialization
    *  for all tests). **/

   /*
    * Set Up
    */
   @Before public void testparameters() {
      double[] weights = new double[]{6,4,2,3,5,7,1};
      String[] terms = new String[]{"ape", "app", "ban", "bat", "bee", "car", "cat"}; 
      n = new Autocomplete.TrieAutocomplete(terms, weights);   
      System.out.println("****All test begin****");
   }

        
   
   
   //top matches test begin 
   
   @Test public void Testfortopmatches() {
      
      String passed1 = "";
      String passed2 = "";
      String passed3 = "";
      String passed4 = "";
      String passed5 = "a";
      String passed6 = "ap";
      String passed7 = "b";
      String passed8 = "d";
      int k1 = 8;  
      int k2 = 1;  
      int k3 = 2; 
      int k4 = 3; 
      int k5 = 1;
      int k6 = 1;
      int k8 = 100; 
      int k7 = 2; 
      
      
      
      Iterable aresult1 = n.topMatches(passed1, k1);   
      String a1[] = new String[] {"car", "ape", "bee", "app", "bat", "ban", "cat"};
      List eresult1 = Arrays.asList(a1);  
      Assert.assertEquals(eresult1, aresult1);
      System.out.println("\ntest passed topmatches 1\n");
          
      Iterable aresult2 = n.topMatches(passed2, k2);
      String a2[] = new String[]{"car"}; 
      List eresult2 = Arrays.asList(a2);  
      Assert.assertEquals(eresult2, aresult2);
      System.out.println("\ntest passed topmatches 2 \n"); 
      
      Iterable aresult3 = n.topMatches(passed3, k3);  
      String a3[] = new String[]{"car", "ape"}; 
      List eresult3 = Arrays.asList(a3);  
      Assert.assertEquals(eresult3, aresult3);
      System.out.println("\n test passed topmatches3 \n");
        
      Iterable aresult4 = n.topMatches(passed4, k4);
      String a4[] = new String[]{"car", "ape", "bee"}; 
      List eresult4 = Arrays.asList(a4);  
      Assert.assertEquals(eresult4, aresult4);
      System.out.println("\n test passed topmatches4 \n");
         
      Iterable<String> aresult5 = n.topMatches(passed5, k5);
      String a5[] = new String[]{"ape"}; 
      List eresult5 = Arrays.asList(a5);   
      Assert.assertEquals(eresult5, aresult5);
      System.out.println("\n test passed topmatches5 \n");
           
      Iterable aresult6 = n.topMatches(passed6, k6);
      
      String a6[] = new String[]{"ape"}; 
      List eresult6 = Arrays.asList(a6);
      Assert.assertEquals(eresult6, aresult6);
      System.out.println("\n test passed topmatches6 \n");   
        
      Iterable aresult7 = n.topMatches(passed7, k7);   
      String a7[] = new String[]{"bee", "bat"}; 
      List eresult7 = Arrays.asList(a7);  
      Assert.assertEquals(eresult7, aresult7);
      System.out.println("\n test passed topmatches7 \n");
     
      Iterable aresult8 = n.topMatches(passed8, k8);
      String a8[] = new String[]{}; 
      List eresult8 = Arrays.asList(a8);
        
      Assert.assertEquals(eresult8, aresult8);
      System.out.println("\n test passed topmatches8 \n");
   }
   
   
      //top matche test begin 
   
   @Test public void testsfortopmatch() {  
      Assert.assertEquals("", n.topMatch(" "));
      System.out.println("test1 passed");
      
      Assert.assertEquals("car", n.topMatch(""));
      System.out.println("test2 passed");
      
      Assert.assertEquals("ape", n.topMatch("a"));
      System.out.println("test3 passed");
   
      Assert.assertEquals("ape", n.topMatch("ap"));
      System.out.println("test4 passed");
   
      Assert.assertEquals("bee", n.topMatch("b"));
      System.out.println("test5 passed");
   
      Assert.assertEquals("bat", n.topMatch("ba"));
      System.out.println("test6 passed");
   
      Assert.assertEquals("", n.topMatch("d"));
      System.out.println("test7 passed");
   
   
   }
}