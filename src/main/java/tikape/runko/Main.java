package tikape.runko;

import java.awt.image.IndexColorModel;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
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
import tikape.runko.domain.Vastaus;

public class Main {
    
       public static void main(String[] args) throws Exception {
           
        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
          // käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:forum.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        }
        
        Database database = new Database(jdbcOsoite);
        
        database.setDebugMode(true);
        
        KeskustelualueDao alueDao = new KeskustelualueDao(database);
        KeskusteluavausDao avausDao = new KeskusteluavausDao(database);
        VastausDao vastausDao = new VastausDao(database);

        HashMap indexData = new HashMap<>();
        
        get("/", new TemplateViewRoute() {
            @Override
            public ModelAndView handle(Request req, Response res) throws Exception {                
                List<Keskustelualue> alueet = alueDao.findAllOrderByName();
                
                // Calculate the amount of messages in each Keskustelualue and save it
                laskeViestit(alueet);
                
                //Find the date of the latest message
                etsiViimeisinViesti(alueet);
                
                indexData.put("alueet", alueet);
                
                return new ModelAndView(indexData, "index");
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
            
            /**
             * Finds the latest message in Keskustelualue and saves it
             * 
             * @param alueet List of Keskustelualue-objects for which to find the latest messages
             * @throws SQLException
             * @throws ParseException 
             */
            public void etsiViimeisinViesti(List<Keskustelualue> alueet) throws SQLException, ParseException{
                for (Keskustelualue keskustelualue : alueet) {
                    Date viimeisin = null;
                    for (Keskusteluavaus avaus : avausDao.findAllFromAlue(keskustelualue.getId())) {
                        Date uusinAvauksessa = vastausDao.findLatestMessageTimestampFromAvaus(avaus.getId());
                        
                        if(uusinAvauksessa == null){
                            continue;
                        }
                        if(viimeisin == null){
                            viimeisin = uusinAvauksessa;
                            continue;
                        }
                        if(uusinAvauksessa.before(viimeisin)){
                            viimeisin = uusinAvauksessa;
                        }
                    }
                    keskustelualue.setViimeisinViesti(viimeisin);
                }
            }
        }, new ThymeleafTemplateEngine());
        
             
        get("/keskustelualue/:id", (req, res) ->{
            HashMap map = new HashMap<>();
            Keskustelualue alue = alueDao.findOne(Integer.parseInt(req.params(":id")));
            map.put("keskustelualue", alue);
            
            List<Keskusteluavaus> avaukset;
            
            if(req.queryParams("sivu") != null){
                int sivu = Integer.parseInt(req.queryParams("sivu"));
                avaukset = avausDao.findNewestWithLimitFromAlue(alue.getId(), sivu, 10);
            } else {
                avaukset = avausDao.findNewestWithLimitFromAlue(alue.getId(), 10);
            }

            // Count the messages in every Keskusteluavaus and save the count
            for (Keskusteluavaus keskusteluavaus : avaukset) {
                int viestit = vastausDao.findTheAmountOfMessagesUnder(keskusteluavaus.getId());
                
                keskusteluavaus.setViestimaara(viestit);
            }
            
            // Find the date of the latest messages in each Keskusteluavaus and save the date
            for (Keskusteluavaus keskusteluavaus : avaukset) {
                keskusteluavaus.setViimeisinViesti(vastausDao.findLatestMessageTimestampFromAvaus(keskusteluavaus.getId()));
            }
            
            map.put("avaukset", avaukset);

            return new ModelAndView(map, "keskustelualue");
        }, new ThymeleafTemplateEngine());
    
        get("/keskusteluavaus/:id", (req, res) -> {
            HashMap<String, Object> data = new HashMap<>();
            Keskusteluavaus avaus = avausDao.findOne(Integer.parseInt(req.params(":id")));
            
            data.put("avaus", avaus);
            data.put("viestit", vastausDao.findAllInAvaus(avaus.getId()));
                        
            return new ModelAndView(data, "keskusteluavaus");
            
        }, new ThymeleafTemplateEngine());
        
        post("/", (req, res) -> {
            if(req.queryParams().contains("nimi")){
                String nimi = req.queryParams("nimi");
            
                alueDao.save(new Keskustelualue(nimi));

                res.redirect("/");
            } else if(req.queryParams().contains("etsittavaAlueDaoTestId")){
                if(indexData.containsKey("results")) indexData.remove("results");
                
                int id = Integer.parseInt(req.queryParams("etsittavaAlueDaoTestId"));
                
                ArrayList<Keskustelualue> loydetyt = new ArrayList<Keskustelualue>();
                loydetyt.add(alueDao.findOne(id));
                
                indexData.put("results", loydetyt);
                
                res.redirect("/");
            } else if(req.queryParams().contains("lisattavaAlueDaoTestId")){
                if(indexData.containsKey("results")) indexData.remove("results");
                
                String nimi = req.queryParams("lisattavaAlueDaoTestId");
                alueDao.save(new Keskustelualue(nimi));
                
                res.redirect("/");
            } else if(req.queryParams().contains("poistettavaAlueDaoTestId")){
                if(indexData.containsKey("results")) indexData.remove("results");
                
                int id = Integer.parseInt(req.queryParams("poistettavaAlueDaoTestId"));
                alueDao.delete(id);
                
                res.redirect("/");
            }
                        
            return null;
        });
        
        post("/keskustelualue/:id", (req, res) -> {
            int alueId = Integer.parseInt(req.params(":id"));
            String otsikko = req.queryParams("otsikko");
            String viesti = req.queryParams("viesti");
            String nimimerkki = req.queryParams("nimimerkki");
            
            Keskusteluavaus avaus = new Keskusteluavaus(alueId, otsikko);
                     
            int avausId = avausDao.saveAndGetGeneratedId(avaus);
            
            vastausDao.save(new Vastaus(avausId, viesti, nimimerkki));
            
            res.redirect("/keskusteluavaus/"+avausId);
            
            return null; 
        });
        
        post("/keskusteluavaus/:id", (req, res) -> {
           int avausId = Integer.parseInt(req.params(":id"));
           String viesti = req.queryParams("viesti");
           String nimimerkki = req.queryParams("nimimerkki");
           
           vastausDao.save(new Vastaus(avausId, viesti, nimimerkki));
           
           res.redirect("/keskusteluavaus/"+avausId);
            
            return null; 
        });
       }
}
