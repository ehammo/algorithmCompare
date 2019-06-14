package cin.ufpe.br.interfaces;

import java.util.HashMap;

public interface CloudletUpdateService {
    HashMap<String, byte[]> updateClassificators(String[] newInstances);
}
