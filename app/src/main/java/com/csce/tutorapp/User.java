package com.csce.tutorapp;

/**
 * Created by willh_000 on 10/19/2016.
 *
 * Stores the information about the user for easy database entry.
 *
 */

public class User {

    /* user information variables */
    private String id; //same UID that Firebase assigns users
    private String firstName;
    private String lastName;
    private String email;

    private String accountType; //whether or not the user is a student or a tutor (or both)
    private String[] studentSubjects; //what the user wants to be tutored in as a student
    private String[] tutorSubjects; //what subjects the user wants to tutor students in

    private float studentRating; //rating from tutors of this user's teachability
    private float tutorRating; //rating from students of this user's teaching skills

    /* whether or not this account has been initialized (user has or hasn't created their profile)*/
    private boolean isProfileCreated;

    /* construct a new User object from a Firebase id */
    public User(String uid){
        id = uid;
        isProfileCreated = false;
        firstName = "";
        lastName = "";
        email = "";
        accountType = "";
        studentSubjects = new String[] {"hey"};
        tutorSubjects = new String[] {"heythere"};
        studentRating = 3.f;
        tutorRating = 3.f;
    }

    /* gets the user's id */
    public String getID(){
        return id;
    }

    /* update user profile minimum*/
    public void updateProfile(String fName, String lName, String newEmail){
        isProfileCreated = true;
        firstName = fName;
        lastName = lName;
        email = newEmail;
    }

    /* update all user profile info */
    public void updateProfile(String fName, String lName, String newEmail, String actType, String[] stuSubjects, String[] tutrSubjects){
        isProfileCreated = true;
        firstName = fName;
        lastName = lName;
        email = newEmail;
        accountType = actType;
        studentSubjects = stuSubjects;
        tutorSubjects = tutrSubjects;
    }
}
