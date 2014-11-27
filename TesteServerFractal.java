package comanche.fractal;

public class TesteServerFractal {

  public static void main(String[] args) {

    RequestAnalyzer ra = new RequestAnalyzer();
    RequestDispatcher rd = new RequestDispatcher();
    FileRequestHandler frh = new FileRequestHandler();
    ErrorRequestHandler erh = new ErrorRequestHandler();
    Logger l = new BasicLogger();

    ra.bindFc("rh", rd);
    ra.bindFc("l", l);
    rd.bindFc("h0", frh);
    rd.bindFc("h1", erh);

    IFrame iFrame = new InteropFrame(ra);
    // new ServerPadraoRMI();
    //iFrame.startServer(6666);
    //iFrame.connectServer(parameters, pBinding, ra);
  }
}