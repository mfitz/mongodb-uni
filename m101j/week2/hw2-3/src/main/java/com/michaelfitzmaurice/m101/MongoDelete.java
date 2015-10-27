package com.michaelfitzmaurice.m101;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.orderBy;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDelete {

  public static void main(String[] args) {
    
    MongoClient client = new MongoClient("localhost", 27017);
    MongoDatabase db = client.getDatabase("students");
    MongoCollection<Document> grades = db.getCollection("grades");
    System.out.println("\tGrades collection size: " + grades.count());
    
    Bson filter = eq("type", "homework");
    Bson sort = orderBy( ascending("student_id"), ascending("score") );
    List<Document> homeworks = 
        grades
          .find(filter)
          .sort(sort)
          .into( new ArrayList<Document>() );
    System.out.println("\tFound " + homeworks.size() + " homework grades");
    
    int studentId = -1;
    for (Document document : homeworks) {
      System.out.println(document);
      int id = document.getInteger("student_id");
      if (id == (studentId) ) {
        continue;
      } else {
        System.out.println("Deleting homework: " + document);
        grades.deleteOne(document);
      }
      
      studentId = id;
    }
    
    System.out.println("\tGrades collection new size: " + grades.count());
    
    client.close();
  }

}
