package org.example;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class XmlHandler {
    private static final SimpleDateFormat fmt = new SimpleDateFormat("dd:MM:yy");

    public static void saveToXML(List<AbstractItem> items, String filename) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("wagons");
        doc.appendChild(root);

        for (AbstractItem it : items) {
            if (!(it instanceof Wagon)) continue;
            Wagon w = (Wagon) it;
            Element we = doc.createElement("wagon");
            we.setAttribute("id", String.valueOf(w.getId()));

            Element type = doc.createElement("type"); type.setTextContent(w.getType()); we.appendChild(type);
            Element model = doc.createElement("model"); model.setTextContent(w.getModel()); we.appendChild(model);
            Element capacity = doc.createElement("capacity"); capacity.setTextContent(String.valueOf(w.getCapacity())); we.appendChild(capacity);
            Element seats = doc.createElement("seats"); seats.setTextContent(String.valueOf(w.getSeats())); we.appendChild(seats);

            String dstr = w.getReleaseDate() == null ? "" : fmt.format(w.getReleaseDate());
            Element release = doc.createElement("releaseDate"); release.setTextContent(dstr); we.appendChild(release);

            Element price = doc.createElement("price"); price.setTextContent(String.valueOf(w.getPrice())); we.appendChild(price);

            root.appendChild(we);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(doc), new StreamResult(new File(filename)));
    }

    public static List<Wagon> loadFromXML(String filename) throws Exception {
        List<Wagon> list = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists()) throw new IllegalArgumentException("File not found: " + filename);

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(f);
        NodeList nodes = doc.getElementsByTagName("wagon");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            int id = Integer.parseInt(e.getAttribute("id"));
            String type = getText(e, "type");
            String model = getText(e, "model");
            double capacity = Double.parseDouble(getText(e, "capacity"));
            int seats = Integer.parseInt(getText(e, "seats"));
            String ds = getText(e, "releaseDate");
            Date d = ds.isEmpty() ? null : fmt.parse(ds);
            double price = Double.parseDouble(getText(e, "price"));
            list.add(new Wagon(id, type, model, capacity, seats, d, price));
        }
        return list;
    }

    private static String getText(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if (nl.getLength() == 0) return "";
        return nl.item(0).getTextContent();
    }
}
