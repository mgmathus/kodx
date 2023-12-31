/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vertigo.pdftools.main;

import vertigo.kodc.ui.Advance;
import vertigo.kodc.ui.Working;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

/**
 *
 * @author pyro
 */
public class Core {

    /**
     * @param args the command line arguments
     */

    Working frameWorking = new Working();

    public Core() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = frameWorking.getSize().width;
        int h = frameWorking.getSize().height;
        int x = (dim.width - w) / 2;
        int y = (dim.height - h) / 2;
        frameWorking.setLocation(x, y);
    }



    public Working getFrame1() {
        return frameWorking;
    }


    public int buildPDF(List<String> images, String filename, Advance ad) {
        Document document = new Document();

        try {
            if (!filename.endsWith(".pdf")) {
                filename = filename + ".pdf";
            }
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            document.setMargins(ad.getLeft(), ad.getRight(), ad.getUp(), ad.getDown());
            Rectangle pageSize = new Rectangle(document.getPageSize());
            pageSize.setBackgroundColor(new com.itextpdf.text.BaseColor(ad.getMycolor().getRGB()));
            document.setPageSize(pageSize);

            images.forEach(image -> {
                try {
                    Image image1 = Image.getInstance(image);
                    System.out.println(image1.getUrl());
                    document.add(image1);
                    document.newPage();
                }catch (final Exception e){
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            });

            document.close();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public void extractImages(String inFile, String destination) throws IOException {
        PDDocument document = null;
        document = PDDocument.load(inFile);
        List<PDPage> pages = document.getDocumentCatalog().getAllPages();


        pages.forEach(page -> {
            int counter = 0;
            try {
                PDResources resources = page.getResources();
                Map pageImages = resources.getImages();
                if (pageImages != null) {
                    Iterator imageIter = pageImages.keySet().iterator();

                    while (imageIter.hasNext()) {
                        counter++;
                        String key = (String) imageIter.next();
                        PDXObjectImage image = (PDXObjectImage) pageImages.get(key);
                        OutputStream out = new FileOutputStream(destination + counter + ".jpg");
                        image.write2OutputStream(out);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    public String getArchivoTexto(String ruta) {
        FileReader fr = null;
        BufferedReader br = null;

        String contenido = "";
        try {
            //ruta puede ser de tipo String o tipo File
            fr = new FileReader(ruta);
            br = new BufferedReader(fr);

            String linea;
            //Obtenemos el contenido del archivo linea por linea
            while ((linea = br.readLine()) != null) {
                contenido += linea + "\n";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } //finally se utiliza para que si todo ocurre correctamente o si ocurre 
        //algun error se cierre el archivo que anteriormente abrimos
        finally {
            try {
                br.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return contenido;
    }


    public void splitPDF(String name,String file,String dir) {
        try {
            PdfReader reader = new PdfReader(file);
            int n = reader.getNumberOfPages();
            int i = 0;
            while (i < n) {
                String outFile = name.substring(0, name.indexOf(".pdf"))
                        + "-" + String.format("%03d", i + 1) + ".pdf";
                Document document = new Document(reader.getPageSizeWithRotation(1));
                PdfCopy writer = new PdfCopy(document, new FileOutputStream(dir+outFile));
                document.open();
                PdfImportedPage page = writer.getImportedPage(reader, ++i);
                writer.addPage(page);
                document.close();
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int mergePDF(List<String> files, String filename){
        try {
          if (!filename.endsWith(".pdf")) {
                filename = filename + ".pdf";
            }
          Document PDFCombineUsingJava = new Document();
          PdfCopy copy = new PdfCopy(PDFCombineUsingJava, new FileOutputStream(filename));
          PDFCombineUsingJava.open();

          files.forEach(file -> {
              try {
                  PdfReader ReadInputPDF = new PdfReader(file);
                  int number_of_pages = ReadInputPDF.getNumberOfPages();
                  for (int page = 0; page < number_of_pages; ) {
                      copy.addPage(copy.getImportedPage(ReadInputPDF, ++page));
                  }
              }catch(Exception e){
                  e.printStackTrace();
              }
          });

          PDFCombineUsingJava.close();
        }
        catch (Exception i)
        {
            System.out.println(i);
            return -1;
        }
        return 0;
    }

    public boolean isImage(String path) {
        File f = new File(path);
        String mimetype = new MimetypesFileTypeMap().getContentType(f);
        String[] types = mimetype.split("/");

        if (types.length >0 && types[0].equals("image")) {
            return true;
        } else {
            return false;
        }
    }

     public boolean isPDF(String path) {
        File f = new File(path);
        String mimetype = new MimetypesFileTypeMap().getContentType(f);

        if (mimetype.equals("application/octet-stream")) {
            return true;
        } else {
            return false;
        }
    }
}


