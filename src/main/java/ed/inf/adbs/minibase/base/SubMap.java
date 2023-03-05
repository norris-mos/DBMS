package ed.inf.adbs.minibase.base;

import java.util.List;
import java.util.Map;

public class SubMap {

 

    private List<Map<Term,Term>> mappinglist;
   
    
  

    


    public void Submap(List<Map<Term, Term>> mappinglist) {
        this.mappinglist = mappinglist;


    }
    
    public List<Map<Term, Term>> getMap(){
        return mappinglist;
    }


    
}
