package com.mycompany.knn;

//basic metric interface

public interface Metric {
	double getDistance(Record s, Record e);
}