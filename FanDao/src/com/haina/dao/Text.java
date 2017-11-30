package com.haina.dao;


import java.util.List;

import com.haina.domin.Student;
public class Text {
	public static void main(String[] args) {
		Student stu = new Student();
		stu.setSno(19);
		stu.setSname("zz");
		Dao d = new Dao();
		//System.out.println(d.selectavg(stu, "sno"));
		//d.save(stu);
		//ss s = new ss();
	    //d.save(s);
           List<Student> list =  d.selectlike(stu);
      /* Long in = d.selectcount(stu);
       System.out.println(in);*/
		//d.delete(stu2);
	    //d.update(stu,stu2);	
		//d.find(stu2);
		 //  List<Student> list =  d.selectlimitand(stu, 1, 5);
	      
   //List<Student> list =  d.selectGB(stu, "sno");
	       for(Student l : list){
	    	  System.out.println(l.getSno());
	    	  System.out.println(l.getSname());
	    	  System.out.println(l.getSsex());
	    	  System.out.println(l.getSage());
	          }
	}
}
