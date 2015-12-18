package nettyServer;

import java.util.Map;
import java.util.Queue;

/**
 * Creates a hardcoded string of HTML page /status. 
 * @author rk
 *
 */
public class StatusPage {
	
	private static final String PART1 = "<!DOCTYPE html>" +
		"<html>"+
		"<head>"+
		    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
		    "<title>Server statistic</title>" +
		    "<meta charset=\"utf-8\">" +
		    "<style type=\"text/css\">" +
		        "body {" +
		            "margin: 0;" +
		            "padding: 25px 0;" +
		        "}" +
		        "table {" +
		            "border-spacing: 1px;" +
		            "color: #000;" +
		            "width: 100%;" +
		        "}" +
		        ".main {" +
		            "margin: 0 auto;" +
		            "width: 1000px;" +
		        "}" +
		        ".main caption {" +
		            "background: #FFF;" +
		            "font-weight: 700;" +
		            "padding: 5px;" +
		        "}" +
		        ".main__info {" +
		            "border-spacing: 0;" +
		        "}" +
		        ".main__info td {" +
		            "background: #FFF;" +
		            "padding: 5px;" +
		            "text-align: center;" +
		            "font-size: 20px;" +
		        "}" +
		        ".main__info td > span {" +
		            "font-weight: 700;" +
		        "}" +
		        ".main__ip th, .main__url th, .main__logs th {" +
		            "background: #6495ED;" +
		            "padding: 5px;" +
		        "}" +
		        ".main__ip td, .main__url td, .main__logs td {" +
		            "background: #B0E0E6;" +
		            "padding: 5px;" +
		            "text-align: center;" +
		        "}" +
		    "</style>" +
		"</head>" +
		"<body>" +
		"<table class=\"main\">" +
		    "<tbody>" +
		    "<tr>" +
		        "<td>" +
		            "<table class=\"main__info\">";
	
	private static final String PART2 = "</table>" +
		        "</td>" +
		    "</tr>" +
		    "<tr>" +
		        "<td>" +
		            "<table class=\"main__ip\">" +
		                "<caption>THE NUMBER OF REQUESTS RECEIVED FROM IP</caption>" +
		                "<tbody>" +
			                "<tr>" +
			                    "<th>IP</th>" +
			                    "<th>Requests Number</th>" +
			                    "<th>Time of last request</th>" +
			                "</tr>";
	
	private static final String PART3 = 
						"</tbody>" + 
					"</table>" +
				"</td>" +
			"</tr>" +
			"<tr>" +
				"<td>" +
					"<table class=\"main__url\">" +
					    "<caption>THE NUMBER OF REDIRECTIONS TO URL</caption>" +
					    "<tbody>" +
					        "<tr>" +
					            "<th>URL</th>" +
					            "<th>Number of Redirections</th>" +
					        "</tr>";
	
	private static final String PART4 = 
						"</tbody>" +
		            "</table>" +
		        "</td>" +
		    "</tr>" +
		    "<tr>" +
		        "<td>" +
		            "<table class=\"main__logs\">" +
		                "<caption>16 LAST CONNECTIONS INFO</caption>" +
		                "<tbody>" +
			                "<tr>" +
			                    "<th>SRC IP</th>" +
			                    "<th>URI</th>" +
			                    "<th>Timestamp</th>" +
			                    "<th>Sent Bytes</th>" +
			                    "<th>Received bytes</th>" +
			                    "<th>Speed (bytes/sec)</th>" +
			                "</tr>";
	
	private static final String PART5 = 
						"</tbody>" +
		            "</table>" +
		        "</td>" +
		    "</tr>" +
		    "</tbody>" +
	      "</table>" +
          "</body>" +
          "</html>";
	
	public static String getStatusPage() {
		StringBuilder sb = new StringBuilder(PART1);
		sb.append("<tbody>" +
				  	"<tr>" +
				  		"<td>Total requests: <span>" + ServerStatistics.getInstance().getAllRequests() + "</span></td>" +
		                "<td>Unique Requests: <span>" + ServerStatistics.getInstance().getUniqueRequestsCount() + "</span></td>" +
		                "<td>Connections currently opened: <span>" + ServerStatistics.getInstance().getOpenConnectionsCount() + "</span></td>" +
		            "</tr>" +
		          "</tbody>");
		sb.append(PART2);
		
		Map<String, IpInfo> uniqueIps = ServerStatistics.getInstance().getIpStatistics();
		for(Map.Entry<String, IpInfo> entry : uniqueIps.entrySet()) {
			sb.append("<tr>" + 
			          	"<td>" + entry.getKey() + "</td>" +
			            "<td>" + entry.getValue().getRequestsCount() + "</td>" +                        
			            "<td>" + entry.getValue().getTimeOfLastRequest() + "</td>" +
			         "</tr>");
		}
		
		sb.append(PART3);
		
		Map<String, Integer> redirects = ServerStatistics.getInstance().getRedirectsStatistics();
		if(redirects.isEmpty()) {
			sb.append("<tr>" + 
		          	"<td>" + "-" + "</td>" +
		            "<td>" + "-" + "</td>" +                        
		         "</tr>");
		}
		for(Map.Entry<String, Integer> entry : redirects.entrySet()) {
			sb.append("<tr>" + 
			          	"<td>" + entry.getKey() + "</td>" +
			            "<td>" + entry.getValue() + "</td>" +                        
			         "</tr>");
		}
		
		sb.append(PART4);
		
		Queue<ConnectionInfo> connections = ServerStatistics.getInstance().getConnectionsLog();
		for(ConnectionInfo info : connections) {
			sb.append("<tr>" + 
			          	"<td>" + info.getSrcIp() + "</td>" +
			          	"<td>" + info.getUri() + "</td>" +
			            "<td>" + info.getDate() + "</td>" +   
			            "<td>" + info.getReceivedBytes() + "</td>" +
			            "<td>" + info.getSentBytes() + "</td>" +                        
			            "<td>" + info.getSpeed() + "</td>" +
			         "</tr>");
		}
		
		sb.append(PART5);
		
		return sb.toString();
	}
}
