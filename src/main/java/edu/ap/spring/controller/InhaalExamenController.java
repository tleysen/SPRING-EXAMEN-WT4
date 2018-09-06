package edu.ap.spring.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import edu.ap.spring.model.*;
import edu.ap.spring.redis.RedisService;

@Controller
@Scope("session")
public class InhaalExamenController {

   private RedisService service; // pattern : "exams":exam:student:reason:date
		
   @Autowired
   public void setRedisService(RedisService service) {
	   this.service = service;
   }

   public InhaalExamenController() {}
 
   @RequestMapping("/list")
   @ResponseBody
   public String listExams(@RequestParam("student") String student) {	   
	   List<InhaalExamen> exams = new ArrayList<InhaalExamen>();
	   Set<String> keys = service.keys("exams:*");
	   for(String key : keys) {
		   String[] parts = key.split(":");
		   if(parts[2].equalsIgnoreCase(student)) {
			   exams.add(new InhaalExamen(parts[2], parts[1], parts[3], parts[4]));
		   }
	   }
	   exams.sort(Comparator.comparing(InhaalExamen::getReason));
	   
	   StringBuilder b = new StringBuilder();
	   b.append("<html><body><table>");
	   b.append("<tr><th>Datum</th><th>Student</th><th>Examen</th><th>Reden</th></tr>");
	   
	   for(InhaalExamen ex : exams) {
		   b.append("<tr>");
		   		b.append("<td>");
		   			b.append(ex.getDate());
		   		b.append("</td>");
		   		b.append("<td>");
		   			b.append(ex.getStudent());
		   		b.append("</td>");
		   		b.append("<td>");
		   			b.append(ex.getExam());
		   		b.append("</td>");
		   		b.append("<td>");
		   			b.append(ex.getReason());
		   		b.append("</td>");
		   b.append("</tr>");
	   }
	   b.append("</table></body></html>");
	   
	   return b.toString();
   }
   
   @RequestMapping("/listall")
   @ResponseBody
   public String listAllExams() {
	   List<InhaalExamen> exams = new ArrayList<InhaalExamen>();
	   Set<String> keys = service.keys("exams:*");
	   for(String key : keys) {
		   String[] parts = key.split(":");
		   exams.add(new InhaalExamen(parts[2], parts[1], parts[3], parts[4]));
	   }
	   exams.sort(Comparator.comparing(InhaalExamen::getStudent));
	   
	   StringBuilder b = new StringBuilder();
	   b.append("<html><body><table>");
	   b.append("<tr><th>Datum</th><th>Student</th><th>Examen</th><th>Reden</th></tr>");
	   
	   for(InhaalExamen ex : exams) {
		   b.append("<tr>");
		   		b.append("<td>");
		   			b.append(ex.getDate());
		   		b.append("</td>");
		   		b.append("<td>");
		   			b.append(ex.getStudent());
		   		b.append("</td>");
		   		b.append("<td>");
		   			b.append(ex.getExam());
		   		b.append("</td>");
		   		b.append("<td>");
		   			b.append(ex.getReason());
		   		b.append("</td>");
		   b.append("</tr>");
	   }
	   b.append("</table></body></html>");
	   
	   return b.toString();
   }
   
   @PostMapping("/new")
   @ResponseBody
   public String makeReservation(@RequestParam("student") String student, 
		   						@RequestParam("exam") String exam,
		   						@RequestParam("reason") String reason) {
	   
	   String pattern = "exams:" + exam + ":" + student + ":" + reason + ":*";
	   Set<String> keys = service.keys(pattern);
	   if(keys.size() == 0) {
		   InhaalExamen newExam = new InhaalExamen(student, exam, reason);
		   service.setKey("exams:" + exam + ":" + student + ":" + reason + ":" + newExam.getDate(), "");
		   return "InhaalExamen toegevoegd";
	   }
	   else {
		   return "InhaalExamen bestaat reeds!";
	   }
   }
}
