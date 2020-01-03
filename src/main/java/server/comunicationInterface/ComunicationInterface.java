package server.comunicationInterface;

import org.json.simple.JSONObject;
import server.interfaces.observerServer;

public class ComunicationInterface implements observerServer{
    
    public ComunicationInterface() {
        
    }

    @Override
    public void update(int acao, JSONObject Ob) {
        
        switch(acao){
            
            case 1:
                String output = (String) Ob.get("output");
                System.out.println(output);
                break;
                
            case 2:
                System.out.println("Pedido de Ligao ao DS.");
                break;
                
            case 4:
                String excepcao = (String) Ob.get("exception");
                System.out.println(excepcao);
                break;
                
            default:
                System.out.println("Default!");
        
        }
        
    }
    
}
