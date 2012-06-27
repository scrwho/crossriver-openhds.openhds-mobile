package org.openhds.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
	
	private static final String TAG = "DatabaseAdapter";
		
	private static final String DATABASE_NAME = "entityData";
	private static final String DATABASE_PATH = "/data/data/org.openhds.activity/databases/";
	
	private static final String DATABASE_TABLE_INDIVIDUAL = "individual";
	private static final String INDIVIDUAL_UUID = "uuid";  
	private static final String INDIVIDUAL_EXTID = "extId";  
	private static final String INDIVIDUAL_FIRSTNAME = "firstName";  
	private static final String INDIVIDUAL_LASTNAME = "lastName";  
	private static final String INDIVIDUAL_DOB = "dob";  
	private static final String INDIVIDUAL_GENDER = "gender";
	private static final String INDIVIDUAL_MOTHER = "mother";  
	private static final String INDIVIDUAL_FATHER = "father";  
	private static final String INDIVIDUAL_RESIDENCE = "currentResidence";  
	
	private static final String DATABASE_TABLE_LOCATION = "location";
	private static final String LOCATION_UUID = "uuid";  
	private static final String LOCATION_EXTID = "extId";  
	private static final String LOCATION_NAME = "name";  
	private static final String LOCATION_LATITUDE = "latitude";  
	private static final String LOCATION_LONGITUDE = "longitude";  
	private static final String LOCATION_HIERARCHY = "hierarchy";	
	
	private static final String DATABASE_TABLE_HIERARCHY = "hierarchy";
	private static final String HIERARCHY_UUID = "uuid";  
	private static final String HIERARCHY_EXTID = "extId";  
	private static final String HIERARCHY_NAME = "name";  
	private static final String HIERARCHY_PARENT = "parent";  
	private static final String HIERARCHY_LEVEL = "level";  

	private static final String DATABASE_TABLE_SOCIALGROUP = "socialgroup";
	private static final String DATABASE_TABLE_VISIT = "visit";
	private static final String DATABASE_TABLE_RESIDENCY = "residency";
	private static final String DATABASE_TABLE_HIERARCHYLEVEL = "hierarchyLevel";
	 
	private static final int DATABASE_VERSION = 1;
	 
	private static final String INDIVIDUAL_CREATE =
	        "create table individual (uuid text primary key, " + 
	        "extId text not null, firstname text not null, lastname text not null, " +
	        "dob text not null, gender text not null, mother text not null, " +
	        "father text not null, currentResidence text not null, " +
	        "foreign key(mother) references individual(uuid), " +
	        "foreign key(father) references individual(uuid), " +
	        "foreign key(currentResidence) references location(uuid));";
	
	private static final String LOCATION_CREATE =
        "create table location (uuid text primary key, " + 
        "extId text not null, name text not null, latitude text, " +
        "longitude text, hierarchy text not null);";
	
	private static final String HIERARCHY_CREATE =
	        "create table hierarchy (uuid text primary key, " + 
	        "extId text not null, name text not null, parent text not null, " +
	        "level text not null, foreign key(parent) references hierarchy(uuid));";
	 
	private DatabaseHelper dbHelper;
	private SQLiteDatabase database;
	private Context context;
	 
	public DatabaseAdapter(Context context) {
		this.context = context;
		dbHelper = new DatabaseHelper(context);
	}
	
	public DatabaseAdapter open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	    return this;
	}

	public void close() {
		dbHelper.close();
	    database.close();
	}
	 
	public long createIndividual(String uuid, String extId, String firstName, 
				String lastName, String dob, String gender, String mother, String father, String residence) {
		 		
		 ContentValues values = new ContentValues();
		 values.put(INDIVIDUAL_UUID, uuid);
		 values.put(INDIVIDUAL_EXTID, extId);
		 values.put(INDIVIDUAL_FIRSTNAME, firstName);
		 values.put(INDIVIDUAL_LASTNAME, lastName);
		 values.put(INDIVIDUAL_DOB, dob);
		 values.put(INDIVIDUAL_GENDER, gender);
		 values.put(INDIVIDUAL_MOTHER, mother);
		 values.put(INDIVIDUAL_FATHER, father);
		 values.put(INDIVIDUAL_RESIDENCE, residence);
		 Log.i(TAG, "inserting into individual with extId " + extId);
		 return database.insert(DATABASE_TABLE_INDIVIDUAL, null, values);	
	}
	 
	public long createLocation(String uuid, String extId, String name, String latitude, 
			 String longitude, String hierarchy) {
		 
		 ContentValues values = new ContentValues();
		 values.put(LOCATION_UUID, uuid);
		 values.put(LOCATION_EXTID, extId);
		 values.put(LOCATION_NAME, name);
		 values.put(LOCATION_LONGITUDE, longitude);
		 values.put(LOCATION_LATITUDE, latitude);
		 values.put(LOCATION_HIERARCHY, hierarchy);
		 Log.i(TAG, "inserting into location with extId " + extId);
		 return database.insert(DATABASE_TABLE_LOCATION, null, values);
	 }
	 
	 public long createHierarchy(String uuid, String extId, String name, String parent, 
			 String level) {
		 
		 ContentValues values = new ContentValues();
		 values.put(HIERARCHY_UUID, uuid);
		 values.put(HIERARCHY_EXTID, extId);
		 values.put(HIERARCHY_NAME, name);
		 values.put(HIERARCHY_PARENT, parent);
		 values.put(HIERARCHY_LEVEL, level);
		 Log.i(TAG, "inserting into hierarchy with extId " + extId);
		 return database.insert(DATABASE_TABLE_HIERARCHY, null, values);
	 }
	 
	 public List<String> getAllRegions(String levelName) {
		 open();
		 List<String> regions = new ArrayList<String>();
		 
		 String query = "select * from hierarchy where level = '" + levelName + "';";
		 Cursor cursor = database.rawQuery(query, null);
		 
		 if (cursor.moveToFirst()) {
			 do {
				 regions.add(cursor.getString(2));
			 } while (cursor.moveToNext());
		 }
		 cursor.close();
		 close();
		 return regions;
	 } 	 
	 	 
	 public SQLiteDatabase getDatabase() {
		 return database;
	 }

	 public void setDatabase(SQLiteDatabase database) {
		 this.database = database;
	 }

	 private static class DatabaseHelper extends SQLiteOpenHelper {
				 	 
		 public DatabaseHelper(Context context) {
			 super(context, DATABASE_NAME, null, DATABASE_VERSION);
		 }
		 
		 @Override
		 public void onCreate(SQLiteDatabase db) {
			 db.execSQL(INDIVIDUAL_CREATE);
			 db.execSQL(LOCATION_CREATE);
			 db.execSQL(HIERARCHY_CREATE);
		 }
	
		 @Override
		 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			 db.execSQL("drop table if exists " + DATABASE_TABLE_INDIVIDUAL);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_LOCATION);
			 db.execSQL("drop table if exists " + DATABASE_TABLE_HIERARCHY);
		     onCreate(db);
		 }
	 }
}
