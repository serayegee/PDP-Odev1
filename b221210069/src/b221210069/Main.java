/** 
*  @author Seray Eğe seray.ege@ogr.sakarya.edu.tr
*  @since 18.03.2024
*  <p> 
*  Main metodu içeren sınıf
*  </p>
*/

package b221210069;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// Kullanıcıdan URL ve klonlanacak dizinin adını al
        Scanner scanner = new Scanner(System.in);
        System.out.println("GitHub deposunun URL'sini girin:");
        String depoUrl = scanner.nextLine();
        System.out.println("Klonlanacak dizinin adını girin:");
        String klonDizinAdi = scanner.nextLine();
        scanner.close();

        
        // Git depoyu klonlama
        try {
            ProcessBuilder builder = new ProcessBuilder("git", "clone", depoUrl, klonDizinAdi);
            // Klonlanacak depo dizini - user.home
            builder.directory(new File(System.getProperty("user.home")));
            builder.redirectErrorStream(true);

            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Depo başarıyla klonlandı.\n\n");

                // Klonlanma başarılıysa .java dosyaları getirilsin
                File klonDizini = new File(System.getProperty("user.home") + File.separator + klonDizinAdi);
                if (klonDizini.exists() && klonDizini.isDirectory()) {
                    dosyalariAnalizEt(klonDizini);
                } else {
                    System.out.println("Klonlanan dizin bulunamadı.");
                }

            } else {
                System.out.println("Depo klonlanırken bir hata oluştu. Git çıkış kodu: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Depo klonlanırken bir hata oluştu:");
            e.printStackTrace();
        }
    }

    private static void dosyalariAnalizEt(File dosya) {
        if (dosya.isDirectory()) {
            File[] altDosyalar = dosya.listFiles();
            if (altDosyalar != null) {
                for (File altDosya : altDosyalar) {
                    dosyalariAnalizEt(altDosya);
                }
            }
        } else if (dosya.getName().endsWith(".java")) {
            //System.out.println(dosya.getAbsolutePath());
            analiz(dosya);
        }
    }


    private static void analiz(File file) {
        // Dosyanın içeriğini oku ve sınıf tanımlamalarını bul
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean classFound = false;
            int yorumSatiriSayisi = 0;
            int kodSatiriSayisi = 0;
          
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("/*") || line.trim().startsWith("//")) {
                    yorumSatiriSayisi++;
                } else if (line.trim().isEmpty()) {
                    // Boş satırı atla
                    continue;
                } else {
                    if (line.contains("class ")) {
                        classFound = true;
                    }
                    kodSatiriSayisi++;
                }
            }
            // Sınıf varsa ve içeriğinde yorum satırı bulunuyorsa analiz yap
            if (classFound && (yorumSatiriSayisi > 0 || kodSatiriSayisi > 0)) {
            	 int yildizSayisi = yildizSayisi(getFileContent(file));
            	 int yorumSatiriSayisii = yorumSatiriSayisii(getFileContent(file));
            	 int kodSatiriSayisii = kodSatiriSayisii(getFileContent(file));
            	 int loc = loc(getFileContent(file));
            	 int fonksiyonSayisi = fonksiyonSayisi(getFileContent(file));
            	// double yg=((double)((double)(yildizSayisi+yorumSatiriSayisii)*0.8)/fonksiyonSayisi);
            	// double yh=((double)(double)(kodSatiriSayisii/fonksiyonSayisi)*0.3);
            	 double yorumSapmaYuzdesi = hesaplaYorumSapmaYuzdesi(yildizSayisi, yorumSatiriSayisii, kodSatiriSayisii, fonksiyonSayisi);

                System.out.println("Sınıf: " + file.getName());
                System.out.println("Javadoc Satır Sayısı: " + yildizSayisi);
                System.out.println("Yorum Satırı Sayısı: " + yorumSatiriSayisii);
                System.out.println("Kod Satırı Sayısı: " + kodSatiriSayisii);
                System.out.println("LOC: " + loc );
                System.out.println("Fonksiyon Sayısı: " + fonksiyonSayisi);
               // double toplamSatirSayisi = yorumSatiriSayisi + kodSatiriSayisi;
              //  System.out.println("Toplam satır sayısı: " + toplamSatirSayisi);
               // System.out.println("Yorum sapma yüzdesi: " + ((double) ((double)(100*yg)/yh)-100  ));
                System.out.println("Yorum Sapma Yüzdesi: " + yorumSapmaYuzdesi);
                System.out.println("----------------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

	}
    
    private static String getFileContent(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private static int yildizSayisi(String content) {
    	
    	int yildizSayisi = 0;
        boolean javadocSatiri = false;

        String[] satirlar = content.split("\n");
        for (String satir : satirlar) {
            if (satir.trim().startsWith("/**")) {
                javadocSatiri = true;
                continue;
            }

            if (javadocSatiri && !satir.trim().startsWith("*/")) {
                yildizSayisi += (satir.length() - satir.replaceAll("\\*", "").length());
            }

            if (satir.trim().startsWith("*/")) {
                javadocSatiri = false;
            }
        }

        return yildizSayisi;

    	    
    }
    
    private static int yorumSatiriSayisii(String content) {
    	  int yorumSatiriSayisii = 0;
    	    boolean javadocSatiri = false;

    	    String[] satirlar = content.split("\n");
    	    for (String satir : satirlar) {
    	        if (satir.trim().startsWith("/**")) {
    	            javadocSatiri = true;
    	            // `/**` satırı bir yorum satırı olarak sayılmayacak
    	            continue;
    	        }

    	        if (javadocSatiri) {
    	            // `/**` ve `*/` arasındaki satırları atla
    	            if (satir.trim().startsWith("*/")) {
    	                javadocSatiri = false;
    	            }
    	            continue;
    	        }

    	        // Satır içinde "//" varsa 
    	        if (satir.contains("//")) {
    	            yorumSatiriSayisii++;
    	        }

    	        // `/*` ile başlayan satırlar 
    	        if (satir.trim().startsWith("/*")) {
    	            yorumSatiriSayisii++;
    	        }
    	    }

    	    return yorumSatiriSayisii;
    }
    
    private static int kodSatiriSayisii(String content) {
        int kodSatiriSayisii = 0;
        boolean javadocSatiri = false;

        String[] satirlar = content.split("\n");
        for (String satir : satirlar) {
            if (satir.trim().startsWith("/**")) {
                javadocSatiri = true;
                // `/**` satırı bir yorum satırı olarak sayılmayacak
                continue;
            }

            if (javadocSatiri) {
                // `/**` ve `*/` arasındaki satırları atla
                if (satir.trim().startsWith("*/")) {
                    javadocSatiri = false;
                }
                continue;
            }

            // Satır // ile başlıyorsa
            if (satir.trim().startsWith("//")) {
                continue;
            }

            // Satır içinde "/*" varsa yorum satırı olarak sayılmayacak
            if (satir.trim().startsWith("/*")) {
                continue;
            }
            
            if (satir.trim().startsWith("*")) {
                continue;
            }

            // Boşluk, yorum ve javadoc satırlarını saymayacak, geri kalanlar kod satırı
            if (!satir.trim().isEmpty()) {
                kodSatiriSayisii++;
            }
        }

        return kodSatiriSayisii;
    }
    
    private static int loc(String content) {
        int loc = 0;

        String[] satirlar = content.split("\n");
        for (String satir : satirlar) {
            loc++;
        }

        return loc;
    }
    
    
    private static int fonksiyonSayisi(String content) {
        int fonksiyonSayisi = 0;

        String[] satirlar = content.split("\n");
        for (String satir : satirlar) {
            // Satırda fonksiyon tanımı var mı?
            if (satir.trim().matches(".*\\b\\w+\\s+(\\w+)\\s*\\(.*?\\)\\s*\\{.*")) {
                fonksiyonSayisi++;
            }
        }

        return fonksiyonSayisi;
    }
    
    

    private static double hesaplaYorumSapmaYuzdesi(int yildizSayisi, int yorumSatiriSayisii, int kodSatiriSayisii, int fonksiyonSayisi) {
        double YG = ((yildizSayisi + yorumSatiriSayisii) * 0.8) / fonksiyonSayisi;
        double YH = (kodSatiriSayisii / (double) fonksiyonSayisi) * 0.3; // double dönüşümü burada yapılıyor
        return ((100 * YG) / YH) - 100;
    }



}

