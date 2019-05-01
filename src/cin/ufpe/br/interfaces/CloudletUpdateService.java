package cin.ufpe.br.interfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public interface CloudletUpdateService {
    HashMap<String, byte[]> updateClassificators(String[] newInstances);
}
