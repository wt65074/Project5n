// Will Tobey
// wtobey1@jhu.edu

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class HashMapTest extends  MapTestBase {

   @Before
   public void newMap() {

      map = new HashMap<Integer, Integer>();

   }
}