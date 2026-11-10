import java.util.*;

class DocumentEditor{
    Document doc;
    Persistence storage;
    
    public DocumentEditor(Document doc, Persistence storage){
        this.doc = doc;
        this.storage = storage;
    }

    public void addText(String text){
        doc.addElement(new TextElement(text));
    }

    public void addImage(String imagePath){
        doc.addElement(new ImageElement(imagePath));
    }

    public String renderDocument(){
        return doc.render();
    }

    public void save(){
        storage.save(doc.render());
    }
}

class Document{
    ArrayList<DocumentElement> elements;

    public Document(){
        this.elements = new ArrayList<>();
    }

    public void addElement(DocumentElement element){
        elements.add(element);
        return;
    }

    public String render(){
        StringBuilder sb = new StringBuilder();

        for(DocumentElement e: elements){
            sb.append(e.render());
        }
        
        return sb.toString();
    }
}

interface DocumentElement{
    String render();
}

class TextElement implements DocumentElement{
    String text;

    TextElement(String text){
        this.text = text;
        return;
    }

    public String render(){
        System.out.println(text);
        return text;
    }
}

class ImageElement implements DocumentElement{
    String imagePath;

    ImageElement(String imagePath){
        this.imagePath = imagePath;
    }

    public String render(){
        System.out.println("Image path: " + imagePath);
        return "Image path: " + imagePath;
    }
}

interface Persistence{
    void save(String data);
}

class FileStorage implements Persistence{
    public void save(String data){
        System.out.println("Saving data to file: " + data);
        return;
    }
}

class DBStorage implements Persistence{
    public void save(String data){
        System.out.println("Saving data to database: " + data);
        return;
    }
}

public class LLD1{
    public static void main(String[] args){
        Document doc = new Document();
        Persistence storage = new FileStorage();

        DocumentEditor editor = new DocumentEditor(doc, storage);

        editor.addText("Hello, World!");
        editor.addImage("/path/to/image.png");
        editor.renderDocument();
        editor.save();
    }
}