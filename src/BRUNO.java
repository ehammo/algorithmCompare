import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class BRUNO {

//	public static String[] csvs = {"Energia_3MP.csv","Energia_8.5MP.csv","Time_3MP.csv","Time_8.5MP.csv"};
//	public static String[] pastas = {"BenchFace"};
		public static String[] csvs = {"Energia_2MP.csv","Energia_8MP.csv","Time_2MP.csv","Time_8MP.csv"};
		public static String[] pastas = {"BenchImage"};


	public static void main(String[] args) throws IOException {

		for(String pasta : pastas){
			for(String csv : csvs){
				Path file = Paths.get(pasta,csv);
				System.out.println(pasta);
				Double media=(double)0,sd=(double)0;


				List<String> lines = Files.lines(file).collect(Collectors.toList());
				lines = lines.subList(1,lines.size());
				for(String line : lines){
					String[] tokens = line.split(";");
					if(tokens[3].contains("e")){
						break;
					}
					media = Double.parseDouble(tokens[3].replace(",", "."));
					System.out.println("media: "+ media);
					sd = Double.parseDouble(tokens[4].replace(",", "."));
					System.out.println("sd: "+ sd);
					System.out.println(tokens[0]+" "+tokens[1]+" "+tokens[2]);

					int size = 20;

					//NAO MECHE
					double total = 20;
					int quantidade = 1;
					Random r = new Random();
					double rangeMin = media-sd;
					double rangeMax = media+sd;
					for(int i=0;i<size;i++){
						quantidade++;
						Double tentativa =(double) 0;
						if(quantidade%20==0){
							tentativa =((media*quantidade) - total);
							System.out.println("hey");
						}else{
							tentativa = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
						}
						if(tentativa<Math.floor(media)){
							i--;
							break;
						}
						total+=tentativa;
						System.out.println((tentativa.toString()).replace('.', ','));
					}
				}
			}
		}
	}

}
