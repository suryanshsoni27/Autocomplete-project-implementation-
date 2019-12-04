import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.*;
import java.util.NoSuchElementException;


public class Autocomplete {
        /**
         * Uses binary search to find the index of the first Term in the passed in
         * array which is considered equivalent by a comparator to the given key.
         * This method should not call comparator.compare() more than 1+log n times,
         * where n is the size of a.
         * 
         * @param a
         *            - The array of Terms being searched
         * @param key
         *            - The key being searched for.
         * @param comparator
         *            - A comparator, used to determine equivalency between the
         *            values in a and the key.
         * @return The first index i for which comparator considers a[i] and key as
         *         being equal. If no such index exists, return -1 instead.
         */
   public static int firstIndexOf(Term[] a, Term key, Comparator<Term> comparator) {
            // TODO: Implement firstIndexOf
      int beg = 0, end = a.length-1;
      int index = -1;
      while (beg <= end) {
         int mid = (beg + end)/2;
         Term cur = a[mid];
         int comparisonResult = comparator.compare(key, cur); 
         if (comparisonResult == 0) index = mid;
         if (comparisonResult <= 0) end = mid-1;
         else beg = mid+1;
      } 
      return index;
   }

        /**
         * The same as firstIndexOf, but instead finding the index of the last Term.
         * 
         * @param a
         *            - The array of Terms being searched
         * @param key
         *            - The key being searched for.
         * @param comparator
         *            - A comparator, used to determine equivalency between the
         *            values in a and the key.
         * @return The last index i for which comparator considers a[i] and key as
         *         being equal. If no such index exists, return -1 instead.
         */
   public static int lastIndexOf(Term[] a, Term key, Comparator<Term> comparator) {
            // TODO: Implement lastIndexOf
      int beg = 0, end = a.length-1;
      int index = -1;
      while (beg <= end) {
         int mid = (beg + end)/2;
         Term cur = a[mid];
         int comparisonResult = comparator.compare(key, cur); 
         if (comparisonResult == 0) index = mid;
         if (comparisonResult < 0) end = mid-1;
         else beg = mid+1;
      }  
      return index;
   }

    /**
     * An Autocompletor supports returning either the top k best matches, or the
     * single top match, given a String prefix.
     * 
     * @author Austin Lu
     *
     */
   public interface Autocompletor {
   
        /**
         * Returns the top k matching terms in descending order of weight. If there
         * are fewer than k matches, return all matching terms in descending order
         * of weight. If there are no matches, return an empty iterable.
         */
      public Iterable<String> topMatches(String prefix, int k);
   
        /**
         * Returns the single top matching term, or an empty String if there are no
         * matches.
         */
      public String topMatch(String prefix);
   
        /**
         * Return the weight of a given term. If term is not in the dictionary,
         * return 0.0
         */
      public double weightOf(String term);
   } 
    /**
     * Implements Autocompletor by scanning through the entire array of terms for
     * every topKMatches or topMatch query.
     */
   public static class BruteAutocomplete implements Autocompletor {
   
      Term[] myTerms;
   
      public BruteAutocomplete(String[] terms, double[] weights) {
         if (terms == null || weights == null)
            throw new NullPointerException("One or more arguments null");
         if (terms.length != weights.length)
            throw new IllegalArgumentException("terms and weights are not the same length");
         myTerms = new Term[terms.length];
         HashSet<String> words = new HashSet<String>();
         for (int i = 0; i < terms.length; i++) {
            words.add(terms[i]);
            myTerms[i] = new Term(terms[i], weights[i]);
            if (weights[i] < 0)
               throw new IllegalArgumentException("Negative weight "+ weights[i]);
         }
         if (words.size() != terms.length)
            throw new IllegalArgumentException("Duplicate input terms");
      }
   
      public Iterable<String> topMatches(String prefix, int k) {
         if (k < 0)
            throw new IllegalArgumentException("Illegal value of k:"+k);
            // maintain pq of size k
         PriorityQueue<Term> pq = new PriorityQueue<Term>(k, new Term.WeightOrder());
         for (Term t : myTerms) {
            if (!t.getWord().startsWith(prefix))
               continue;
            if (pq.size() < k) {
               pq.add(t);
            } else if (pq.peek().getWeight() < t.getWeight()) {
               pq.remove();
               pq.add(t);
            }
         }
         int numResults = Math.min(k, pq.size());
         LinkedList<String> ret = new LinkedList<String>();
         for (int i = 0; i < numResults; i++) {
            ret.addFirst(pq.remove().getWord());
         }
         return ret;
      }
   
      public String topMatch(String prefix) {
         String maxTerm = "";
         double maxWeight = -1;
         for (Term t : myTerms) {
            if (t.getWeight() > maxWeight && t.getWord().startsWith(prefix)) {
               maxWeight = t.getWeight();
               maxTerm = t.getWord();
            }
         }
         return maxTerm;
      }
   
      public double weightOf(String term) {
         for (Term t : myTerms) {
            if (t.getWord().equalsIgnoreCase(term))
               return t.getWeight();
         }
            // term is not in dictionary return 0
         return 0;
      }
   }
   /**
     * 
     * Using a sorted array of Term objects, this implementation uses binary search
     * to find the top term(s).
     * 
     * @author Austin Lu, adapted from Kevin Wayne
     * @author Jeff Forbes
     */
   public static class BinarySearchAutocomplete implements Autocompletor {
   
      Term[] myTerms;
   
        /**
         * Given arrays of words and weights, initialize myTerms to a corresponding
         * array of Terms sorted lexicographically.
         * 
         * This constructor is written for you, but you may make modifications to
         * it.
         * 
         * @param terms
         *            - A list of words to form terms from
         * @param weights
         *            - A corresponding list of weights, such that terms[i] has
         *            weight[i].
         * @return a BinarySearchAutocomplete whose myTerms object has myTerms[i] =
         *         a Term with word terms[i] and weight weights[i].
         * @throws a
         *             NullPointerException if either argument passed in is null
         */
      public BinarySearchAutocomplete(String[] terms, double[] weights) {
         if (terms == null || weights == null)
            throw new NullPointerException("One or more arguments null");
         myTerms = new Term[terms.length];
         for (int i = 0; i < terms.length; i++) {
            myTerms[i] = new Term(terms[i], weights[i]);
         }
         Arrays.sort(myTerms);
      }
   
        /**
         * Required by the Autocompletor interface. Returns an array containing the
         * k words in myTerms with the largest weight which match the given prefix,
         * in descending weight order. If less than k words exist matching the given
         * prefix (including if no words exist), then the array instead contains all
         * those words. e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then
         * topKMatches("b", 2) should return {"bell", "bat"}, but topKMatches("a",
         * 2) should return {"air"}
         * 
         * @param prefix
         *            - A prefix which all returned words must start with
         * @param k
         *            - The (maximum) number of words to be returned
         * @return An array of the k words with the largest weights among all words
         *         starting with prefix, in descending weight order. If less than k
         *         such words exist, return an array containing all those words If
         *         no such words exist, reutrn an empty array
         * @throws a
         *             NullPointerException if prefix is null
         */
      public Iterable<String> topMatches(String prefix, int k) {
         if (prefix == null) throw new NullPointerException();
         int f = firstIndexOf(myTerms, new Term(prefix, 0) , new Term.PrefixOrder(prefix.length()));
         int l = lastIndexOf(myTerms, new Term(prefix, 0) , new Term.PrefixOrder(prefix.length()));
         if (l < 0) 
            return new ArrayList<String>();
         PriorityQueue<Term> pq = new PriorityQueue<Term>(k, new Term.WeightOrder());
         for (int i = f; i <= l; i++) {
            Term t = myTerms[i];
            if (pq.size() < k) {
               pq.add(t);
            } else if (pq.peek().getWeight() < t.getWeight()) {
               pq.remove();
               pq.add(t);
            }
         }
         int numResults = Math.min(k, pq.size());
         LinkedList<String> ret = new LinkedList<String>();
         for (int i = 0; i < numResults; i++) {
            ret.addFirst(pq.remove().getWord());
         }
         return ret;
      }
   
        /**
         * Given a prefix, returns the largest-weight word in myTerms starting with
         * that prefix. e.g. for {air:3, bat:2, bell:4, boy:1}, topMatch("b") would
         * return "bell". If no such word exists, return an empty String.
         * 
         * @param prefix
         *            - the prefix the returned word should start with
         * @return The word from myTerms with the largest weight starting with
         *         prefix, or an empty string if none exists
         * @throws a
         *             NullPointerException if the prefix is null
         * 
         */
      public String topMatch(String prefix) {
         if (prefix == null) throw new NullPointerException();
         int f = firstIndexOf(myTerms, new Term(prefix, 0) , new Term.PrefixOrder(prefix.length()));
         int l = lastIndexOf(myTerms, new Term(prefix, 0) , new Term.PrefixOrder(prefix.length()));
         ArrayList<Term> found = new ArrayList<Term>();
         if (l < 0) 
            return "";
         double maxWeight = myTerms[f].getWeight();
         int maxWeightIndex = f;
         for (int i = f+1; i <= l; i++) {
            if (myTerms[i].getWeight() > maxWeight) {
               maxWeight = myTerms[i].getWeight();
               maxWeightIndex = i;
            }
         }
         return myTerms[maxWeightIndex].getWord();
      }
   
        /**
         * Return the weight of a given term. If term is not in the dictionary,
         * return 0.0
         */
      public double weightOf(String term) {
            // TODO complete weightOf
         return 0.0;
      }
   }
    /**
     * General trie/priority queue algorithm for implementing Autocompletor
     * 
     * @author Austin Lu
     * @author Jeff Forbes
     * 
     */
   public static class TrieAutocomplete implements Autocompletor {
   
        /**
         * Root of entire trie
         */
      protected Node myRoot;
   
        /**
         * Constructor method for TrieAutocomplete. Should initialize the trie
         * rooted at myRoot, as well as add all nodes necessary to represent the
         * words in terms.
         * 
         * @param terms
         *            - The words we will autocomplete from
         * @param weights
         *            - Their weights, such that terms[i] has weight weights[i].
         * @throws NullPointerException
         *             if either argument is null
         * @throws IllegalArgumentException
         *             if terms and weights are different weight
         */
      public TrieAutocomplete(String[] terms, double[] weights) {
         if (terms == null || weights == null)
            throw new NullPointerException("One or more arguments null");
            // Represent the root as a dummy/placeholder node
         myRoot = new Node('-', null, 0);
      
         for (int i = 0; i < terms.length; i++) {
            add(terms[i], weights[i]);
         }
      }
   
        /**
         * Add the word with given weight to the trie. If word already exists in the
         * trie, no new nodes should be created, but the weight of word should be
         * updated.
         * 
         * In adding a word, this method should do the following: Create any
         * necessary intermediate nodes if they do not exist. Update the
         * subtreeMaxWeight of all nodes in the path from root to the node
         * representing word. Set the value of myWord, myWeight, isWord, and
         * mySubtreeMaxWeight of the node corresponding to the added word to the
         * correct values
         * 
         * @throws a
         *             NullPointerException if word is null
         * @throws an
         *             IllegalArgumentException if weight is negative.
         */
      private void add(String word, double weight) {//https://www.hackerearth.com/practice/data-structures/advanced-data-structures/trie-keyword-tree/tutorial/
         try {//https://www.geeksforgeeks.org/trie-insert-and-search/
                          //https://leetcode.com/problems/implement-trie-prefix-tree/
                
            if(word != null && weight >=0) {//checks the given condition
               
               Node newnode; //initilizes the new node
               Node n = myRoot; // intilizes node n to given node myRoot
               boolean bool;//initlizes boolean variable bool used to check in condition statemnt 
               int length = word.length();//length paramter initalized as in int takes the value of word length 
               int loop = 0;//this is a loop counter 
               char ch;
               do  { //traverses through the word to see for the character ch                         
                  double subtreewe = n.mySubtreeMaxWeight; //initlizes the subtree weight to the variabe subtree
                  double asweigh = 0.0;boolean booleanweight = false; double check_weight = weight;  
                  int x = 1;//conditon true for switch statemnt which lets the loop enter the condition 
                  switch(x) {
                     case 1:
                        if(check_weight > subtreewe) {
                           booleanweight = check_assign(booleanweight,subtreewe,asweigh,check_weight);   //checks the booleanweight as true if all the valess  supplied to the condtion is true   
                           if(booleanweight){
                              subtreewe= check_weight;
                              n.mySubtreeMaxWeight = assign(booleanweight,asweigh,subtreewe);// assigns the weight of the subtree as subtree weight by passing and checking through assign function
                           }
                          
                        }
                  }               
                  ch = word.charAt(loop);//checks the character at that loop counter or at that instance value 
                  //System.out.println(ch); 
                  Map<Character, Node> ni = n.children; 
                  bool = ni.containsKey(ch);
                  Node tosee = n.getChild(ch);//    // https://stackoverflow.com/questions/31544501/creating-a-node-class-in-java  
                  if(bool == false && tosee == null){
                     newnode = new Node(ch, n, weight);  //creation of new node done here
                     Map<Character,Node> child;//creates a Map child //https://www.geeksforgeeks.org/longest-prefix-matching-a-trie-based-solution-in-java/
                     child = n.children;
                     child.put(ch,newnode);//https://stackoverflow.com/questions/31544501/creating-a-node-class-in-java
                  }
                  
                  n = n.getChild(ch);
                  
                  loop++;
                  
               }while(loop<length);
               
               
               n.isWord=true;//sets isWord to true.
               
               n.setWeight(weight);//sets the value for setWeight by passing the weight
               
               n.setWord(word); //sets the value setWord by passing the word 
               
               
               if(word == null) {
                  throw new NullPointerException("Null pointer exception being thrown "); //checks if the word is null
               }
            
               if(weight < 0) {
                  throw new IllegalArgumentException("Illegal argument exception being thrown");//checks if the weight is negative
               }
            
            }
         }
         catch (Exception e) {
            if(e == new NullPointerException()) {
               System.out.println("word is null");
            }
            else if(e == new IllegalArgumentException()) {
               System.out.println("weight is negative");
            }
         }
            
      }
      
      boolean check_assign(boolean wei,double subtreewe,double asweigh,double check_weight) {//externally created fucntion check_Assign
         boolean flag = false;
         if(wei == false)
            flag = true;
         
         return flag;
      }
      
      double assign(boolean weight,double x,double y) {//externally created assign function
         if(weight == true)
            x = y;
         return x;
      }
   
   
        /**
         * Required by the Autocompletor interface. Returns an array containing the
         * k words in the trie with the largest weight which match the given prefix,
         * in descending weight order. If less than k words exist matching the given
         * prefix (including if no words exist), then the array instead contains all
         * those words. e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then
         * topKMatches("b", 2) should return {"bell", "bat"}, but topKMatches("a",
         * 2) should return {"air"}
         * 
         * @param prefix
         *            - A prefix which all returned words must start with
         * @param k
         *            - The (maximum) number of words to be returned
         * @return An Iterable of the k words with the largest weights among all
         *         words starting with prefix, in descending weight order. If less
         *         than k such words exist, return all those words. If no such words
         *         exist, return an empty Iterable
         * @throws a
         *             NullPointerException if prefix is null
         */
      public Iterable<String> topMatches(String prefix, int k) {//https://www.hackerearth.com/practice/data-structures/advanced-data-structures/trie-keyword-tree/tutorial/
         //https://www.geeksforgeeks.org/trie-insert-and-search/
                          //https://leetcode.com/problems/implement-trie-prefix-tree/th.com/practice/data-structures/advanced-data-structures/trie-keyword-tree/tutorial/
             
             
         List List = new ArrayList();//  creates a new arraylist list which is returned by the fucntion after manipulation 
         try { 
            if (prefix != null){ //throw new NullPointerException();   
               Node npref = myRoot;
               boolean bool; 
               PriorityQueue<Node> pque = new PriorityQueue<Node>();//makes priority que , which stores the valeus in reverse order 
               PriorityQueue<Node> AddNode = new PriorityQueue<Node>();//makes priority que Addnode
               pque = new PriorityQueue<Node>(new Node.ReverseSubtreeMaxWeightComparator());// updates the pque so that it stores the values in reverese order user ReverseSubtreeMaxWeightcomaprator
            //https://www.geeksforgeeks.org/implement-priorityqueue-comparator-java/
               char[] arraystring = prefix.toCharArray();//https://crunchify.com/java-simple-way-to-convert-string-to-char-array/
               for (char ch : arraystring)
               {  //https://www.geeksforgeeks.org/hashmap-containskey-method-in-java/
                  Map<Character, Node> nip = npref.children; 
                  bool = nip.containsKey(ch);//creates a bool which checks if character is there in the arraystring
                  Node kid = npref.getChild(ch);// creates a child node of prefix node
                  double weight = npref.mySubtreeMaxWeight;
                  switch (k) {
                     case 0 :
                        if(bool == false) {
                           return List;}
                     default:
                        if(bool == false && k > 0)
                           return List;
                        else
                           npref = kid;}}
               boolean condition = true;
               pque.add(npref);// https://www.tutorialspoint.com/java/util/priorityqueue_add.html
               double lsize = 0;
               do {
                  if(!pque.isEmpty()){
                     if(List.size() <=k) {//throw new NoSuchElementException();
                        npref = pque.remove(); // remove is being used so that is no element exist NoSuchException gets thrown
                        AddNode.add(npref);// node being added in Reresversed order
                        npref = AddNode.poll();// poll is being is used here so that npref can hold a null value before entering the loop is no element exist
                        //https://www.geeksforgeeks.org/priorityqueue-remove-method-in-java/
                        boolean nprefbool = npref.isWord;
                        if (nprefbool){List.add(npref.myWord);
                           double size = List.size();
                           if (size < k)
                              continue;
                           else
                              break;}
                        Collection<Node> ncv;
                        ncv = npref.children.values();  //https://docs.oracle.com/javase/8/docs/api/javax/xml/soap/Node.html
                        pque.addAll(ncv);}
                     else{condition = false;
                        break;}}
                  lsize = pque.size();
               }while(condition == true && lsize > 0 || lsize !=0);}}
         catch (Exception e) {
            if(e == new NullPointerException()) {
               System.out.println("word is null");}}   
         return List;}      
    
        /**
         * Given a prefix, returns the largest-weight word in the trie starting with
         * that prefix.
         * 
         * @param prefix
         *            - the prefix the returned word should start with
         * @return The word from with the largest weight starting with prefix, or an
         *         empty string if none exists
         * @throws a
         *             NullPointerException if the prefix is null
         */
      public String topMatch(String prefix) {//https://www.hackerearth.com/practice/data-structures/advanced-data-structures/trie-keyword-tree/tutorial/
         //https://www.geeksforgeeks.org/trie-insert-and-search/
                          //https://leetcode.com/problems/implement-trie-prefix-tree/th.com/practice/data-structures/advanced-data-structures/trie-keyword-tree/tutorial/
         String lString ="";
         try { 
            if (prefix != null){// this has been used as a reference 
               String word;
               boolean bool;
               Node nodemy = myRoot;
               double prelen = prefix.length();
               double i = 0;
               while(i<prelen) { //traverses through the word to see for the character ch
                  char ch = prefix.charAt((int)i);
                  Map<Character,Node> child = nodemy.children; ////https://www.baeldung.com/java-initialize-hashmap
                  bool = child.containsKey(ch);
                  boolean existence = true;
                  if(bool == false && existence ) {
                     String nothingList = "";
                     return nothingList;}
                  else {nodemy = nodemy.getChild(ch);}// casse when one character is submitted or just nothing is submitted is handled here.
                  i = i + 1;}
               if(nodemy.myWeight == nodemy.mySubtreeMaxWeight) {
                  return nodemy.myWord;}
               PriorityQueue<Node> AddNode = new PriorityQueue<Node>();
               do {
               // https://www.geeksforgeeks.org/iterator-vs-foreach-in-java/
                  Collection<Node> values;
                  values = nodemy.children.values();
                  for(Iterator<Node> n = values.iterator(); n.hasNext();)
                  {Node nc = n.next();
                     if(nc.mySubtreeMaxWeight != nodemy.mySubtreeMaxWeight) {
                        continue;
                     }else {AddNode.add(nc);nodemy = AddNode.remove();
                        break;
                     } }double subweigh = nodemy.mySubtreeMaxWeight;
               } while(nodemy.myWeight != nodemy.mySubtreeMaxWeight || nodemy.isWord == false);
               ArrayList<String> retrieved = new ArrayList<>();
            //String retrieved = nodemy.getWord();
               retrieved.add(nodemy.getWord());
               for (String s: retrieved) {//https://stackoverflow.com/questions/599161/best-way-to-convert-an-arraylist-to-a-string
                  lString += s; 
               }
            }
         } 
         catch (Exception e) {
            if(e == new NullPointerException()) {
               System.out.println("word is null");
            }
         }
         
         return lString;
      }
   
        /**
         * Return the weight of a given term. If term is not in the dictionary,
         * return 0.0
         */
      public double weightOf(String term) {
            // TODO complete weightOf
         return 0.0;
      }
   
        /**
         * Optional: Returns the highest weighted matches within k edit distance of
         * the word. If the word is in the dictionary, then return an empty list.
         * 
         * @param word
         *            The word to spell-check
         * @param dist
         *            Maximum edit distance to search
         * @param k
         *            Number of results to return
         * @return Iterable in descending weight order of the matches
         */
      public Iterable<String> spellCheck(String word, int dist, int k) {
         return null;
      }
      
      
      
      
   
      public Autocomplete.Autocompletor n;
   
   /** Fixture initialization (common initialization
    *  for all tests). **/
   
   /*
    * Set Up
    */
      public void testparameters() {
         double[] weights = new double[]{6,4,2,3,5,7,1};
         String[] terms = new String[]{"ape", "app", "ban", "bat", "bee", "car", "cat"}; 
         n = new Autocomplete.TrieAutocomplete(terms, weights);   
         System.out.println("****All test begin****");
      }
   
        
   
   
   //top matches test begin 
   
      public void Testfortopmatches() {
      
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
         boolean isEqual1 = eresult1.equals(aresult1);
         if(isEqual1 == true)
            System.out.println("\ntest passed topmatches 1 \n"); 
          
         Iterable aresult2 = n.topMatches(passed2, k2);
         String a2[] = new String[]{"car"}; 
         List eresult2 = Arrays.asList(a2);   
         boolean isEqual2 = eresult2.equals(aresult2);
         if(isEqual2 == true)
            System.out.println("\ntest passed topmatches 2 \n"); 
      
         Iterable aresult3 = n.topMatches(passed3, k3);  
         String a3[] = new String[]{"car", "ape"}; 
         List eresult3 = Arrays.asList(a3);  
         boolean isEqual3 = eresult3.equals(aresult3);
         if(isEqual3 == true)
            System.out.println("\ntest passed topmatches 3 \n"); 
        
         Iterable aresult4 = n.topMatches(passed4, k4);
         String a4[] = new String[]{"car", "ape", "bee"}; 
         List eresult4 = Arrays.asList(a4);  
         boolean isEqual4 = eresult4.equals(aresult4);
         if(isEqual4 == true)
            System.out.println("\ntest passed topmatches 4 \n"); 
         
         
         Iterable<String> aresult5 = n.topMatches(passed5, k5);
         String a5[] = new String[]{"ape"}; 
         List eresult5 = Arrays.asList(a5);   
         boolean isEqual5 = eresult5.equals(aresult5);
         if(isEqual5 == true)
            System.out.println("\ntest passed topmatches 5 \n"); 
           
           
         Iterable aresult6 = n.topMatches(passed6, k6);
         String a6[] = new String[]{"ape"}; 
         List eresult6 = Arrays.asList(a6);
         boolean isEqual6 = eresult6.equals(aresult6);
         if(isEqual6 == true)
            System.out.println("\ntest passed topmatches 6 \n");   
        
         Iterable aresult7 = n.topMatches(passed7, k7);   
         String a7[] = new String[]{"bee", "bat"}; 
         List eresult7 = Arrays.asList(a7);  
         boolean isEqual7 = eresult7.equals(aresult7);
         if(isEqual7 == true)
            System.out.println("\ntest passed topmatches 7 \n");   
      
         Iterable aresult8 = n.topMatches(passed8, k8);
         String a8[] = new String[]{}; 
         List eresult8 = Arrays.asList(a8);
        
         boolean isEqual8 = eresult8.equals(aresult8);
         if(isEqual8 == true)
            System.out.println("\ntest passed topmatches 7 \n");   
      
      }
      
      
      //top matche test begin 
      
      public void testsfortopmatch() { 
         String str1 = ""; 
         String str2 = "car"; 
         String str3 = "ape"; 
         String str4 = "ape"; 
         String str5 = "bee"; 
         String str6 = "bat"; 
         String str7 = ""; 
        
         if (str1.equals(n.topMatch(" "))) {
            System.out.println("test1 passed");}
            
         if (str2.equals(n.topMatch(""))) {
            System.out.println("test2 passed");}
      
         if (str3.equals(n.topMatch("a"))) {
            System.out.println("test3 passed");}
            
         if (str4.equals(n.topMatch("ap"))) {
            System.out.println("test4 passed");}
            
            
         if (str5.equals(n.topMatch("b"))) {
            System.out.println("test5 passed");}
            
         if (str6.equals(n.topMatch("ba"))) {
            System.out.println("test6 passed");}
            
         if (str7.equals(n.topMatch("d"))) {
            System.out.println("test7 passed");}         
      
      }
   
   
   
   
      
      
      
   }
   
   
   
}
