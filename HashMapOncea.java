//James Oncea
//joncea1@jhu.edu

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class HashMapOncea<K extends Comparable<? super K>, V>
	implements Map<K, V> {

	private int size;
	private static final int ARRAY_SIZE = 7;
	private int arraySize;
	private Node<K, V>[] nodes;

	private static class Node<K,V> {
		K key;
		V value;

			
		Node(K k, V v) {
        // left and right default to null
        	this.key = k;
            this.value = v;
        }

        //@Override
        public String toString() {
            return "Node<key: " + this.key
                + "; value: " + this.value
               	+ ">";
		}
	}
	public HashMapOncea() {
		this.arraySize = ARRAY_SIZE;
        this.size = 0;
        nodes =  (Node<K,V>[]) new Node[ARRAY_SIZE];
    }


	@Override
    public void insert(K k, V v) throws IllegalArgumentException {
    	if (k == null) {
    		throw new IllegalArgumentException("first argument to insert() is null");
    	}


        // double table size if 50% full
        if (this.size >= arraySize/2) {
        	resize();
        }

        int tmp = hash(k);
        int i = tmp;
        int h = 1;
        do
        {
            if (nodes[i] == null)
            {
                nodes[i] = new Node<K, V>(k, v);
                this.size++;
                return;
            }       
            i = (i + h * h++) % arraySize;            
        } while (i != tmp);
        
    }
    
    @Override
    public V remove(K k) throws IllegalArgumentException {
    	if (k== null) {
    		throw new IllegalArgumentException("argument to remove() is null");
    	}
        if (find(k) < 0) {
    		throw new IllegalArgumentException("Key is not mapped");
        }

        int i = find(k);
        V value = nodes[i].value;       
        nodes[i] = null;

        this.size--; 
        return value;
        
        
    }
    
    @Override
    public void put(K k, V v) throws IllegalArgumentException {
        if(k == null) {
            throw new IllegalArgumentException("argument to put() is null");
        }
    	int index = find(k);
        if(index < 0) {
            throw new IllegalArgumentException("argument to put() is not mapped");
        }
    	nodes[index].value = v;
    }

    /** Finds the index location of a given value.
     *	@param k key to be found
     *  @return returns the index of the keys.
     */
    private int find(K k) {
        
    	int tmp = hash(k);
        int i = tmp;
        int h = 1;
        do
        {
            if(i < 0) {
                return -1;
            }
            if (nodes[i] != null && nodes[i].key.equals(k))
            {
                return i;
            }       
            i = (i + h * h++) % arraySize; 

        } while (i != tmp);
    
        return -1;
        
        
    }

    @Override
    public V get(K k) throws IllegalArgumentException {
    	if (k == null) {
    		throw new IllegalArgumentException("argument to get() is null");
    	}

        int i = find(k);
        if (i > 0) {
            return nodes[i].value;
        }

        return null;
    }

    @Override
    public boolean has(K k) {
    	if (k == null) {
    		throw new IllegalArgumentException("argument to has() is null");
    	} 
       	if(find(k) > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int size() {
    	return this.size;
    }

    // hash function for keys
    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) % arraySize;
    }

    // resizes the hash table to the given capacity by re-hashing all of the keys

    private void resize() {
        
        int newSize = nextPrime(arraySize * 2);
        //System.out.println(arraySize + "newSize " + newSize);
        Node<K, V>[] temp = (Node<K,V>[]) new Node[newSize];
        int currentSize = arraySize;
        arraySize = newSize;
        for (int i = 0; i < currentSize; i++) {
            if (nodes[i] != null) {
                int tmp = hash(nodes[i].key);
                int a = tmp;
                int h = 1;
                 do
                {
                    if (temp[a] == null) {
                        temp[a] = new Node<K, V> (nodes[i].key, nodes[i].value);
                        break;
                    }       
                    a = (a + h * h++) % newSize;            
                } while (a != tmp);
                
            }
        }   
        nodes = temp;
        
    }

     private static int nextPrime( int n )
    {
        if( n <= 0 )
            n = 3;

        if( n % 2 == 0 )
            n++;

        for( ; !isPrime( n ); n += 2 )
            ;

        return n;
    }

    private static boolean isPrime( int n )
    {
        if( n == 2 || n == 3 )
            return true;

        if( n == 1 || n % 2 == 0 )
            return false;

        for( int i = 3; i * i <= n; i += 2 )
            if( n % i == 0 )
                return false;

        return true;
    }

    @Override
    public Iterator<K> iterator() {
        List<K> list = new ArrayList<K>();
        for (int i = 0; i < arraySize; i++) {
            if (nodes[i] != null) {
            	list.add(nodes[i].key);
            }
        }
        return list.iterator();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("{");
        for (Iterator<K> i = iterator(); i.hasNext();) {
            K key = i.next();
            s.append("" + key + ": " + get(key));
            if (i.hasNext()) {
                s.append(", ");
            }
        }
        s.append("}");
        return s.toString();
    }
    /*
    
    public String getString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 9) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static void main(String[] args) { 
        
        int size = 100;
        HashMap<String, Integer> st = new HashMap<String, Integer>(); 
        Random r = new Random();
        String[] ary = new String[size];
        for (int i = 0; i < size; i++) {
            ary[i] =st.getString();
            st.insert(ary[i], 1);
            //System.out.println(ary[i] + " ");
        }
        //System.out.println(st.arraySize);

        for (int i = 0; i < size; i++) {
                int a = st.find(ary[i]);

                System.out.println("Found " + ary[i] + " at " + a);
        }
        //System.out.println("Test");
        System.out.println(st.toString());

        for(int i = 0; i < st.arraySize; i++) {
            if(st.nodes[i] != null) {
                System.out.println(st.nodes[i].key);
            }
        }

        /*
        for(int i = 0; i < 50; i++) {
            System.out.println(st.nextPrime(st.arraySize*2*i));
        }
        */
    
}