package com.michaelfitzmaurice.m101;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class LowestHomeworkScoreRemover {

  public static void main(String[] args) {
    
    MongoClient client = new MongoClient("localhost", 27017);
    MongoDatabase db = client.getDatabase("school");
    MongoCollection<Document> studentsCollection = db.getCollection("students");
    
    List<Document> students = 
        studentsCollection
          .find()
          .into( new ArrayList<Document>() );
    System.out.println("\tFound " + students.size() + " students");
    
    for (Document student : students) {
      int studentId = student.getInteger("_id");
      List<Document> scores = (List<Document>) student.get("scores");
      
      List<Document> editedScores = 
          removeHomeworkWithScore( scores, getLowestHomeworkScore(scores) );
      student.put("scores", editedScores);
      studentsCollection.replaceOne( eq("_id", studentId), student );
    }
    
    client.close();
  }

  private static List<Document> removeHomeworkWithScore(List<Document> scores, double scoreToRemove) {
    
    List<Document> newScores = new ArrayList<Document>();
    
    for (Document score : scores) {
      if ( score.getString("type").equals("homework") 
            && score.getDouble("score") == scoreToRemove) {
          continue;
      }
      
      newScores.add(score);
    }
    
    return newScores;
  }

  private static double getLowestHomeworkScore(List<Document> scores) {
    double lowestHwScore = 100.00;
    for (Document score : scores) {
      if ( score.getString("type").equals("homework") ) {
        double thisScore = score.getDouble("score");
        if (thisScore < lowestHwScore) {
          lowestHwScore = thisScore;
        }
      } else {
        continue;
      }
    }
    
    return lowestHwScore;
  }

}
