using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace InteractBrowser
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();
        }
        private void MainForm_Load(object sender, EventArgs e)
        {
            webBrowser1.Width = this.Width-17;
            webBrowser1.Height = this.Height - 100;
            textBox1.Width = this.Width - 100;
            btn_go.Location =new Point( this.Width - btn_go.Width - 25,btn_go.Location.Y);
            webBrowser1.ScriptErrorsSuppressed = true;
            BackgroundWorker bw = new BackgroundWorker();

            // this allows our worker to report progress during work
           // bw.WorkerReportsProgress = true;

            // what to do in the background thread
            bw.DoWork += new DoWorkEventHandler(
            delegate(object o, DoWorkEventArgs args)
            {
                while (true)
                {
                    //BackgroundWorker b = o as BackgroundWorker;
                    SynchronousSocketListener.StartListening();
                    string returned = SynchronousSocketListener.data;
                    switch (returned.Substring(0,2))
                    {
                        case "up":
                        //case "u-":
                            System.Windows.Forms.SendKeys.SendWait("{PGUP}"); 
                            break;
                        case "do":
                        //case "d-":
                            System.Windows.Forms.SendKeys.SendWait("{PGDN}"); 
                            break;
                        case "ri":
                            webBrowser1.GoForward();
                            //MessageBox.Show("forward");
                            break;
                        case "le":
                            webBrowser1.GoBack();
                            //MessageBox.Show("back");
                            break;
                        case "se":
                            string filter = returned.Substring(3);
                                filter = filter.Replace(' ', '+');
                                webBrowser1.Navigate("https://www.google.com.eg/search?q=" + filter);
                                textBox1.Text = "https://www.google.com.eg/search?q=" + filter;
                            break;
                        case "ma":
                            webBrowser1.Navigate("file:///C:/Users/el-mostafa.org/Desktop/QingLong/InteractBrowser/InteractBrowser/maps2.html");
                            textBox1.Text = "file:///C:/Users/el-mostafa.org/Desktop/QingLong/InteractBrowser/InteractBrowser/maps2.html";
                            break;
                        case "ft":
                            webBrowser1.Navigate("file:///C:/Users/el-mostafa.org/Desktop/QingLong/InteractBrowser/InteractBrowser/maps2.html?r="+returned.Substring(3));
                            textBox1.Text = "file:///C:/Users/el-mostafa.org/Desktop/QingLong/InteractBrowser/InteractBrowser/maps2.html?r=" + returned.Substring(3);
                            break;
                    }
                }

            });
            bw.RunWorkerAsync();
            webBrowser1.Navigate("http://www.google.com");
            textBox1.Text = "http://www.google.com";
        }

        private void btn_go_Click(object sender, EventArgs e)
        {
            webBrowser1.Navigate(textBox1.Text);
        }
    }
}
