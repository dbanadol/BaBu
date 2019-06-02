package com.example.babu;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class PostToDatabase {

    public String uid;
    public String author;
    public String trainingList;

    public PostToDatabase() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public PostToDatabase(String uid, String author, String trainingList) {
        this.uid = uid;
        this.author = author;
        this.trainingList = trainingList;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("trainingList", trainingList);

        return result;
    }
    // [END post_to_map]

}
// [END post_class]
