using System;
using System.Net;
using System.Net.Sockets;
using System.Text;

public class SynchronousSocketListener
{

    // Incoming data from the client.
    public static string data = null;
    private static bool intialized = false;
     // Data buffer for incoming data.
        static byte[] bytes = new Byte[1024];

        // Establish the local endpoint for the socket.
        // Dns.GetHostName returns the name of the 
        // host running the application.
       static IPHostEntry ipHostInfo = Dns.Resolve(Dns.GetHostName());
      static  IPAddress ipAddress = ipHostInfo.AddressList[0];
       static IPEndPoint localEndPoint = new IPEndPoint(ipAddress, 3000);

        // Create a TCP/IP socket.
        static Socket listener = new Socket(AddressFamily.InterNetwork,
            SocketType.Stream, ProtocolType.Tcp);

        static Socket handler;
        
               
    public static void StartListening()
    {
        try{
        if(!intialized)
        { 
            intialized = true;
       
             listener.Bind(localEndPoint);
                listener.Listen(10);
            // Start listening for connections.
            //while (true)
            //{
                Console.WriteLine("Waiting for a connection...");
                // Program is suspended while waiting for an incoming connection.
                 handler = listener.Accept();
        }
               
                do
                {
                    data = null;
                    bytes = new byte[1024];
                    int bytesRec = handler.Receive(bytes);
                    data += Encoding.ASCII.GetString(bytes, 0, bytesRec);
                }
                while (data == "\n");
                // Show the data on the console.
                Console.WriteLine("Text received : {0}", data);

                //handler.Shutdown(SocketShutdown.Both);
              //  handler.Close();
            //}

        }
        catch (Exception e)
        {
            Console.WriteLine(e.ToString());
        }
        }
       // Console.WriteLine("\nPress ENTER to continue...");
       // Console.Read();


}