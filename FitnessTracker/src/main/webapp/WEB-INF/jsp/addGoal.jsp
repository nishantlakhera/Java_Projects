<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Goal</title>
</head>
<body>
<form:form modelAttribute="goal">
 <table>
   <tr>
     <td>Enter Minutes:</td>
     <td><form:input path="minutes"/></td>
   </tr>
   <tr>
     <td colspan="2">
       <input type="submit" value="Enter goal minutes">
     </td>    
   </tr>
 </table>
</form:form>
</body>
</html>