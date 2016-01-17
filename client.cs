		const int PORT_NUM = 5000;
		private TcpClient client1, client2;
		string Ip1, Ip2;
		
		//initialise
		client1 = new TcpClient(Ip1, PORT_NUM);
		client2 = new TcpClient(Ip2, PORT_NUM);


		private void SendData(string data)
		{
			StreamWriter writer1 = new StreamWriter(client1.GetStream());
			StreamWriter writer2 = new StreamWriter(client2.GetStream());
			writer1.Write(data + (char) 13);
			writer1.Flush();
			writer2.Write(data + (char) 13);
			writer2.Flush();
		}