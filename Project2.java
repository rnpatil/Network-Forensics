
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;


public class Project2 {

	private static String sourceIp;
	private static String destIp;
	private static int sourcePort;
	private static int destPort;
	private static HashMap<String,TreeMap<Integer,Packet>> UplinkConnection = new HashMap<String,TreeMap<Integer,Packet>>();
	private static HashMap<String,TreeMap<Integer,Packet>> DownlinkConnection = new HashMap<String,TreeMap<Integer,Packet>>();
	private static TreeMap<Integer, Packet> SequenceData = null;
	private static int size;
	private static int ipcount=0;
	private static int  tcpcount=0;
	private static int udpcount=0;
	private static int packetcount = 0;
	private static  PacketHeader packetHeader=null;
	private static Packet packet=null;
	private static StringBuilder tcpConnection = new StringBuilder();
	private static final int  HTTP_PORT =80;
	private static final String ipEthType="08:00";
	private static final int tcpProtocol=6;
	private static final int  udpProtocol=17;
	private static int pa = 24;
	private static int startByte = 24;
	private static int endByte = 40;

	public static void main(String[] args) throws IOException {


		int task = Integer.parseInt(args[0]);
		int pc;
		String httpRequest="";
		String httpResponse="";
		String host="n/a";
		String URL="";
		String statusCode="";
		boolean multipleReqPacket;
		boolean responsePresent;
		Boolean transferEncoding=false;
		int contentLength=0;
		int downlinkACK = 0;
		long recvTime=0;
		Boolean opaqueData=false;
		StringBuffer downlinkString = new StringBuffer();
		

		//Skip 24 bytes of pcap global header
		pc = 0;
		while (pc++ < 24) {
			System.in.read();
		}

		switch (task) {
		case 1:
			while (System.in.available() > 0) {

				pc = 0;
				byte[] header = new byte[16];

				while (pc < 16) {

					header[pc] = (byte) System.in.read();
					pc++;
				}
				pc=0;
				packetHeader = new PacketHeader(header);
				size = (int) packetHeader.getSize();

				byte[] packetBuffer = new byte[size];

				while (pc < size) {

					packetBuffer[pc] = (byte) System.in.read();
					pc++;
				}

				packet = new Packet(packetBuffer);
				if(packet.getEthType().equals(ipEthType))
				{
					ipcount++;

					if(packet.getProtocol()==tcpProtocol)
					{
						tcpcount++;
						sourceIp=packet.getSourceIp();
						sourcePort = packet.getSourcePort();
						destIp= packet.getDestIp();
						destPort=packet.getDestPort();


						tcpConnection.append(sourceIp+" ");
						tcpConnection.append(String.valueOf(sourcePort)+" ");
						tcpConnection.append(destIp+" ");
						tcpConnection.append(String.valueOf(destPort));


						if(UplinkConnection.containsKey(tcpConnection.toString()))
						{
							SequenceData = UplinkConnection.get(tcpConnection.toString());
							SequenceData.put(packet.getSequenceNumber(),packet);
							UplinkConnection.put(tcpConnection.toString(),SequenceData);
						}
						else
						{
							SequenceData = new TreeMap<Integer,Packet>();
							SequenceData.put(packet.getSequenceNumber(),packet);
							UplinkConnection.put(tcpConnection.toString(),SequenceData);
						}
						tcpConnection.setLength(0);


						tcpConnection.append(sourceIp+" ");
						tcpConnection.append(String.valueOf(sourcePort)+" ");
						tcpConnection.append(destIp+" ");
						tcpConnection.append(String.valueOf(destPort));


						if(!DownlinkConnection.containsKey(tcpConnection.toString()))
						{
							SequenceData = new  TreeMap<Integer,Packet>();
							DownlinkConnection.put(tcpConnection.toString(),SequenceData);
						}
						tcpConnection.setLength(0);


						tcpConnection.append(destIp+" ");
						tcpConnection.append(String.valueOf(destPort)+" ");
						tcpConnection.append(sourceIp+" ");
						tcpConnection.append(String.valueOf(sourcePort));


						if(DownlinkConnection.containsKey(tcpConnection.toString())){
							SequenceData = DownlinkConnection.get(tcpConnection.toString());
							SequenceData.put(packet.getSequenceNumber(),packet);
							DownlinkConnection.put(tcpConnection.toString(),SequenceData);
						}
						else{
							SequenceData = new TreeMap<Integer,Packet>();
							SequenceData.put(packet.getSequenceNumber(),packet);
							DownlinkConnection.put(tcpConnection.toString(),SequenceData);
						}
						tcpConnection.setLength(0);

						tcpConnection.append(destIp+" ");
						tcpConnection.append(String.valueOf(destPort)+" ");
						tcpConnection.append(sourceIp+" ");
						tcpConnection.append(String.valueOf(sourcePort));

						if(!UplinkConnection.containsKey(tcpConnection.toString())){
							SequenceData = new  TreeMap<Integer,Packet>();
							UplinkConnection.put(tcpConnection.toString(),SequenceData);
						}
						tcpConnection.setLength(0);
					}
					else if(packet.getProtocol()==udpProtocol) 
					{
						udpcount++;
					}
				}
				packetcount++;
			}


			System.out.print(packetcount+" "+ipcount+" "+tcpcount+" "+udpcount+" "+(UplinkConnection.size()/2)+"\n");
			break;

		case 2:

			//////////////////
			while (System.in.available() > 0) {

				pc = 0;
				byte[] header = new byte[16];

				while (pc < 16) {

					header[pc] = (byte) System.in.read();
					pc++;
				}
				pc=0;
				packetHeader = new PacketHeader(header);
				size = (int) packetHeader.getSize();

				byte[] packetBuffer = new byte[size];

				while (pc < size) {

					packetBuffer[pc] = (byte) System.in.read();
					pc++;
				}

				packet = new Packet(packetBuffer);
				if(packet.getEthType().equals(ipEthType))
				{
					if(packet.getProtocol()==tcpProtocol)
					{
						sourceIp=packet.getSourceIp();
						sourcePort = packet.getSourcePort();
						destIp= packet.getDestIp();
						destPort=packet.getDestPort();

						if(sourcePort == HTTP_PORT || destPort == HTTP_PORT)
						{
							if(destPort == HTTP_PORT)
							{
								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort)+" ");
								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort));
								if(UplinkConnection.containsKey(tcpConnection.toString()))
								{
									SequenceData = UplinkConnection.get(tcpConnection.toString());
									SequenceData.put(packet.getSequenceNumber(),packet);
									UplinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								else
								{
									SequenceData = new TreeMap<Integer,Packet>();
									SequenceData.put(packet.getSequenceNumber(),packet);
									UplinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);

								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort)+" ");
								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort));

								if(!DownlinkConnection.containsKey(tcpConnection.toString()))
								{
									SequenceData = new  TreeMap<Integer,Packet>();
									DownlinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);
							}

							else if(sourcePort ==HTTP_PORT)  {

								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort)+" ");
								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort));

								if(DownlinkConnection.containsKey(tcpConnection.toString())){
									SequenceData = DownlinkConnection.get(tcpConnection.toString());
									SequenceData.put(packet.getSequenceNumber(),packet);
									DownlinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								else{
									SequenceData = new TreeMap<Integer,Packet>();
									SequenceData.put(packet.getSequenceNumber(),packet);
									DownlinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);

								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort)+" ");
								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort));



								if(!UplinkConnection.containsKey(tcpConnection.toString())){
									SequenceData = new  TreeMap<Integer,Packet>();
									UplinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);
							}
						}
					}
				}
			}

			int uplinkData=0;
			int downlinkData=0;
			StringBuffer finalString = new StringBuffer();
			HashMap<String,String> tcpConnectionData = new HashMap<String,String>();

			for ( String key : UplinkConnection.keySet()) {
				uplinkData =0;
				for(Integer i: UplinkConnection.get(key).keySet()){
					packet=UplinkConnection.get(key).get(i);
					uplinkData+=packet.getDataLength();
					finalString.append(packet.getPayload());
				}
				downlinkData =0;
				for(Integer i: DownlinkConnection.get(key).keySet()){
					packet=DownlinkConnection.get(key).get(i);
					downlinkData+=packet.getDataLength();
					finalString.append(packet.getPayload());
				}

				tcpConnectionData.put(key +" "+uplinkData+" "+downlinkData+"\n",finalString.toString());
				finalString.setLength(0);
			}

			List<String> list = new ArrayList<String>(tcpConnectionData.keySet());
			Collections.sort(list);

			for(String s: list)
				System.out.print(s);

			for(String s: list)
			{
				System.out.write(tcpConnectionData.get(s).getBytes("ISO-8859-1"));
			}
			break;


		case 3:
			while (System.in.available() > 0) {
				pc = 0;
				byte[] header = new byte[16];
				while (pc < 16) {
					header[pc] = (byte) System.in.read();
					pc++;
				}
				pc=0;
				packetHeader = new PacketHeader(header);
				size = (int) packetHeader.getSize();

				byte[] packetBuffer = new byte[size];

				while (pc < size) {

					packetBuffer[pc] = (byte) System.in.read();
					pc++;
				}

				packet = new Packet(packetBuffer);
				if(packet.getEthType().equals(ipEthType))
				{
					if(packet.getProtocol()==tcpProtocol)
					{
						packet.setReqtime(packetHeader.getTime());

						sourceIp=packet.getSourceIp();
						sourcePort = packet.getSourcePort();
						destIp= packet.getDestIp();
						destPort=packet.getDestPort();

						if(sourcePort == HTTP_PORT ||destPort == HTTP_PORT)
						{
							if(destPort == HTTP_PORT)
							{
								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort)+" ");
								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort)+" ");

								if(UplinkConnection.containsKey(tcpConnection.toString())){
									SequenceData = UplinkConnection.get(tcpConnection.toString());
									SequenceData.put(packet.getSequenceNumber(),packet);
									UplinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								else{
									SequenceData = new TreeMap<Integer,Packet>();
									SequenceData.put(packet.getSequenceNumber(),packet);
									UplinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);
								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort)+" ");
								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort)+" ");
								if(!DownlinkConnection.containsKey(tcpConnection.toString())){
									SequenceData = new  TreeMap<Integer,Packet>();
									DownlinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);
							}

							else if(sourcePort == HTTP_PORT)  {

								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort)+" ");
								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort)+" ");

								if(DownlinkConnection.containsKey(tcpConnection.toString())){
									SequenceData = DownlinkConnection.get(tcpConnection.toString());
									SequenceData.put(packet.getSequenceNumber(),packet);
									DownlinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								else{
									SequenceData = new TreeMap<Integer,Packet>();
									SequenceData.put(packet.getSequenceNumber(),packet);
									DownlinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);
								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort)+" ");
								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort)+" ");
								if(!UplinkConnection.containsKey(tcpConnection.toString())){
									SequenceData = new  TreeMap<Integer,Packet>();
									UplinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);
							}
						}
					}
				}
			}


			////////////////////////////////////////////

			TreeMap<Long,String> treeSortedMap = new TreeMap<Long,String>();
			

			for ( String key : UplinkConnection.keySet() ) {


				multipleReqPacket = false;
				responsePresent = false;
				for(Packet packet: UplinkConnection.get(key).values()){

					recvTime=packet.getReqtime();

					if (packet.getDataLength() > 0) {


						if (packet.getPayload().split("\r\n")[0].toUpperCase().endsWith("HTTP/1.1")) {
							multipleReqPacket = false;
						}
						if (multipleReqPacket) {
							downlinkACK += packet.getDataLength();
						} else {
							downlinkACK = packet.getSequenceNumber() + packet.getDataLength();
						}
						httpRequest=packet.getPayload();

						String splitRequest[]=httpRequest.split("\r\n");
						if(splitRequest[0].toUpperCase().endsWith("HTTP/1.1"))
						{
							for(String s :splitRequest)
							{
								if(s.toUpperCase().startsWith("HOST:"))
									host =s.substring(6,s.length()).trim().toLowerCase();
								else if(s.toUpperCase().startsWith("GET"))
									URL =s.substring(4,s.length()-9).trim().toLowerCase();
								else if(s.toUpperCase().startsWith("POST"))
									URL =s.substring(5,s.length()-9).trim().toLowerCase();
								else if(s.toUpperCase().startsWith("PUT"))
									URL =s.substring(4,s.length()-9).trim().toLowerCase();
								else if(s.toUpperCase().startsWith("DELETE"))
									URL =s.substring(7,s.length()-9).trim().toLowerCase();
								else if(s.toUpperCase().startsWith("HEAD"))
								{
									URL =s.substring(5,s.length()-9).trim().toLowerCase();
									opaqueData= true;
								}
							}


							boolean isResp=false;
							for(Packet dwnpacket: DownlinkConnection.get(key).values()){


								if (dwnpacket.getAckNumber() == downlinkACK) {
									responsePresent = true;
									if (dwnpacket.getDataLength() > 0) {
										isResp = true;
										downlinkString.append(dwnpacket.getPayload());
									}
								}
							}
							
							if(responsePresent == false)
							{
								continue;
							}
							if (isResp) {
								httpResponse=downlinkString.toString();
								downlinkString.setLength(0);
								String splitHttpResponse[]=httpResponse.split("\r\n\r\n",2);
								String splitResponseHeader[]=splitHttpResponse[0].split("\r\n");
								transferEncoding=false;
								for(String s :splitResponseHeader)
								{
									if(s.toUpperCase().startsWith("HTTP/1.1"))
									{
										statusCode=s.substring(9,12).trim().toLowerCase();
										if(statusCode.equals("204") || statusCode.equals("304") || statusCode.startsWith("1") )
										{
											opaqueData= true;
											break;
										}
									}
									else if(s.toUpperCase().startsWith("CONTENT-LENGTH:"))
									{
										if(!transferEncoding)
											contentLength =Integer.parseInt(s.substring(16,s.length()).trim());
									}
									else if(s.equalsIgnoreCase("Transfer-Encoding: chunked"))
									{
										transferEncoding=true; contentLength=0; break;
									}
								}

								if(opaqueData)
								{
									contentLength=0;
									transferEncoding=false;
								}

								if(transferEncoding)
								{
									String splitResponseBody= splitHttpResponse[1];
									int index=-1;
									int templength=0;
									while(!splitResponseBody.equals("0\r\n\r\n"))
									{
										index = splitResponseBody.indexOf("\r\n");
										templength=Integer.parseInt(splitResponseBody.substring(0, index).trim(),16);
										contentLength+=templength;

										splitResponseBody=splitResponseBody.substring(index+templength+4);
									}
								}
							}


							if(!treeSortedMap.containsKey(recvTime))
								treeSortedMap.put(recvTime,URL+" "+host+" "+statusCode+" "+contentLength+"\n");
							else
							{
								treeSortedMap.put(recvTime,treeSortedMap.get(recvTime)+ URL+" "+host+" "+statusCode+" "+contentLength+"\n");
							}

							host="n/a";
							URL="";
							recvTime=0;
							downlinkACK=0;
							contentLength=0;
							statusCode="";
							opaqueData=false;
						}
					}
				}

			}

			for(Long i : treeSortedMap.keySet())
				System.out.print(treeSortedMap.get(i));
			break;

		case 4:
			while (System.in.available() > 0) {
				pc = 0;
				byte[] header = new byte[16];
				while (pc < 16) {
					header[pc] = (byte) System.in.read();
					pc++;
				}
				pc=0;
				packetHeader = new PacketHeader(header);
				size = (int) packetHeader.getSize();

				byte[] packetBuffer = new byte[size];

				while (pc < size) {

					packetBuffer[pc] = (byte) System.in.read();
					pc++;
				}

				packet = new Packet(packetBuffer);
				if(packet.getEthType().equals(ipEthType))
				{
					if(packet.getProtocol()==tcpProtocol)
					{
						packet.setReqtime(packetHeader.getTime());

						sourceIp=packet.getSourceIp();
						sourcePort = packet.getSourcePort();
						destIp= packet.getDestIp();
						destPort=packet.getDestPort();

						if(sourcePort == HTTP_PORT ||destPort == HTTP_PORT)
						{
							if(destPort == HTTP_PORT)
							{
								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort)+" ");
								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort)+" ");

								if(UplinkConnection.containsKey(tcpConnection.toString())){
									SequenceData = UplinkConnection.get(tcpConnection.toString());
									SequenceData.put(packet.getSequenceNumber(),packet);
									UplinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								else{
									SequenceData = new TreeMap<Integer,Packet>();
									SequenceData.put(packet.getSequenceNumber(),packet);
									UplinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);
								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort)+" ");
								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort)+" ");
								if(!DownlinkConnection.containsKey(tcpConnection.toString())){
									SequenceData = new  TreeMap<Integer,Packet>();
									DownlinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);
							}

							else if(sourcePort == HTTP_PORT)  {

								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort)+" ");
								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort)+" ");

								if(DownlinkConnection.containsKey(tcpConnection.toString())){
									SequenceData = DownlinkConnection.get(tcpConnection.toString());
									SequenceData.put(packet.getSequenceNumber(),packet);
									DownlinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								else{
									SequenceData = new TreeMap<Integer,Packet>();
									SequenceData.put(packet.getSequenceNumber(),packet);
									DownlinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);
								tcpConnection.append(destIp+" ");
								tcpConnection.append(String.valueOf(destPort)+" ");
								tcpConnection.append(sourceIp+" ");
								tcpConnection.append(String.valueOf(sourcePort)+" ");
								if(!UplinkConnection.containsKey(tcpConnection.toString())){
									SequenceData = new  TreeMap<Integer,Packet>();
									UplinkConnection.put(tcpConnection.toString(),SequenceData);
								}
								tcpConnection.setLength(0);
							}
						}
					}
				}
			}

			///////////////////////////////

			TreeMap<Long,String> timeSortedImages = new TreeMap<Long,String>();
			String statusCodewithDescription="";

			for ( String key : UplinkConnection.keySet() ) {

				multipleReqPacket = false;
				responsePresent = false;
				for(Packet packet: UplinkConnection.get(key).values()){
					recvTime=packet.getReqtime();
					if (packet.getDataLength() > 0) {
						
						if (packet.getPayload().split("\r\n")[0].toUpperCase().endsWith("HTTP/1.1")) {
							multipleReqPacket = false;
						}
						if (multipleReqPacket) {
							downlinkACK += packet.getDataLength();
						} else {
							downlinkACK = packet.getSequenceNumber() + packet.getDataLength();
						}

						httpRequest=packet.getPayload();

						String splitRequest[]=httpRequest.split("\r\n");
						if(splitRequest[0].toUpperCase().endsWith("HTTP/1.1"))
						{
							for(String s :splitRequest)
							{
								if(s.toUpperCase().startsWith("HOST:"))
									host =s.substring(6,s.length()).trim().toLowerCase();
								else if(s.toUpperCase().startsWith("GET"))
									URL =s.substring(4,s.length()-9).trim();
								else if(s.toUpperCase().startsWith("POST"))
									URL =s.substring(5,s.length()-9).trim().toLowerCase();
								else if(s.toUpperCase().startsWith("PUT"))
									URL =s.substring(4,s.length()-9).trim().toLowerCase();
								else if(s.toUpperCase().startsWith("DELETE"))
									URL =s.substring(7,s.length()-9).trim().toLowerCase();
								else if(s.toUpperCase().startsWith("HEAD"))
								{
									URL =s.substring(5,s.length()-9).trim().toLowerCase();
									opaqueData= true;
								}
							}
							if(URL.toLowerCase().endsWith(".png")||URL.toLowerCase().endsWith(".jpeg")||URL.toLowerCase().endsWith(".jpg")||URL.toLowerCase().endsWith(".gif")||URL.toLowerCase().endsWith(".webp") ||URL.toLowerCase().endsWith(".gif")||URL.toLowerCase().endsWith(".gzip"))
							{
								boolean isResp=false;
								for(Packet dwnpacket: DownlinkConnection.get(key).values()){
									if (dwnpacket.getAckNumber() == downlinkACK) {
										responsePresent = true;
										if (dwnpacket.getDataLength() > 0) {
											isResp = true;
											downlinkString.append(dwnpacket.getPayload());
										}

									}
								}
								if(responsePresent == false)
								{
									continue;
								}
								if (isResp) {
									httpResponse=downlinkString.toString();
									downlinkString.setLength(0);
									String splitHttpResponse[]=httpResponse.split("\r\n\r\n",2);
									String splitResponseHeader[]=splitHttpResponse[0].split("\r\n");
									transferEncoding=false;
									for(String s :splitResponseHeader)
									{
										if(s.toUpperCase().startsWith("HTTP/1.1"))
											statusCodewithDescription=s.substring(9).trim();
										else if(s.toUpperCase().startsWith("CONTENT-LENGTH:"))
										{
											if(!transferEncoding)
												contentLength =Integer.parseInt(s.substring(16,s.length()).trim());
										}
										else if(s.equalsIgnoreCase("Transfer-Encoding: chunked"))
										{
											transferEncoding=true; contentLength=0;
										}
									}

									httpResponse=splitHttpResponse[1];

									if(statusCodewithDescription.equals("200 OK"))
									{
										if(opaqueData==true)
										{
											contentLength=0;
											httpResponse="";
											transferEncoding=false;
										}
										if(transferEncoding)
										{
											String splitResponseBody= splitHttpResponse[1];

											int index=-1;
											int templength=0;
											StringBuffer chunkedString = new StringBuffer();
											while(!splitResponseBody.equals("0\r\n\r\n"))
											{
												index = splitResponseBody.indexOf("\r\n");

												templength=Integer.parseInt(splitResponseBody.substring(0, index).trim(),16);
												chunkedString.append(splitResponseBody.substring(index+2,index+2+templength).trim());
												contentLength+=templength;
												splitResponseBody=splitResponseBody.substring(index+templength+4);
											}

											httpResponse=chunkedString.toString();
										}



										if(!timeSortedImages.containsKey(recvTime))
											timeSortedImages.put(recvTime,Integer.toHexString(contentLength)+"\r\n"+httpResponse+"\r\n");
										else
											timeSortedImages.put(recvTime,timeSortedImages.get(recvTime)+Integer.toHexString(contentLength)+"\r\n"+httpResponse+"\r\n");

										host="n/a";
										URL="";
										recvTime=0;
										downlinkACK=0;
										contentLength=0;
										statusCodewithDescription="";
										opaqueData=false;
									}
								}
							}
						}
					}
				}

			}

			timeSortedImages.put(Long.MAX_VALUE,"0\r\n\r\n");

			for(Long s : timeSortedImages.keySet())
			{
				System.out.write(timeSortedImages.get(s).getBytes("ISO-8859-1"));
			}

			break;
		}
	}
}
