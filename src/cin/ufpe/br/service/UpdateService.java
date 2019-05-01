package cin.ufpe.br.service;

import cin.ufpe.br.compare.Retreinamento;
import cin.ufpe.br.interfaces.CloudletUpdateService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class UpdateService implements CloudletUpdateService {

    @Override
    public HashMap<String, byte[]> updateClassificators(String[] newInstances) {
        try {
            // Increase the database
            FileWriter fw = new FileWriter("database_ic.csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out  = new PrintWriter(bw);
            for(String instance : newInstances){
                out.print(instance);
            }
            out.flush();
            out.close();

            //Retrain
            if(newInstances != null) {
                boolean success = Retreinamento.retreinamento();

                //Send new models
                if (success) {
                    return Retreinamento.getModels();
                }
            }
            return Retreinamento.getModels();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
