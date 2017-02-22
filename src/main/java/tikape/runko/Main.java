package tikape.runko;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import static spark.Spark.*;
import spark.TemplateViewRoute;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.KeskustelualueDao;
import tikape.runko.database.KeskusteluavausDao;
import tikape.runko.database.VastausDao;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Keskusteluavaus;
import org.thymeleaf.context.IWebContext;

public class Main {
    
       public static void main(String[] args) throws Exception {
        Database database = new Database("org.sqlite.JDBC", "jdbc:sqlite:forum.db");
        database.setDebugMode(true);
        
        KeskustelualueDao alueDao = new KeskustelualueDao(database);
        KeskusteluavausDao avausDao = new KeskusteluavausDao(database);
        VastausDao vastausDao = new VastausDao(database);

        get("/", new TemplateViewRoute() {
            @Override
            public ModelAndView handle(Request req, Response res) throws Exception {
                HashMap map = new HashMap<>();
                
                List<Keskustelualue> alueet = alueDao.findAll();
                
                // Calculate the amount of messages in each Keskustelualue and save it
                laskeViestit(alueet);
                
                map.put("alueet", alueet);
                
                return new ModelAndView(map, "index");
            }
            
            public void laskeViestit(List<Keskustelualue> alueet) throws SQLException{
                for (Keskustelualue keskustelualue : alueet) {
                    int viestejaAlueessa = 0;

                    int alueId = keskustelualue.getId();

                    List<Keskusteluavaus> avaukset = avausDao.findAllFromAlue(alueId);

                    for (Keskusteluavaus keskusteluavaus : avaukset) {
                        int avausId = keskusteluavaus.getId();

                        int viestejaAvauksessa = vastausDao.findTheAmountOfMessagesUnder(avausId);

                        viestejaAlueessa += viestejaAvauksessa;
                    }

                    keskustelualue.setViestimaara(viestejaAlueessa);
                }
            }
        }, new ThymeleafTemplateEngine());
        
             
        get("/keskustelualue/:id", (req, res) ->{
        HashMap map = new HashMap<>();
        map.put("keskustelualue", alueDao.findOne(Integer.parseInt(req.params(":id"))));
        
        return new ModelAndView(map, "keskustelualue");
        }, new ThymeleafTemplateEngine());
    
       }
}
