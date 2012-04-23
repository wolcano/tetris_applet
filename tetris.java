import java.awt.*;
import java.applet.*;
import java.awt.geom.*;
import java.awt.event.*;

public class tetris extends Applet implements KeyListener
{
	class kocka {
		int x;
		int y;
		int rot;
		int speed;
		Color color;

		public void draw(Graphics2D g) {
			g.setColor(color);
			g.fill(new Rectangle(x, y, sq_w, sq_w));
			g.setColor(Color.black);
			g.draw(new Rectangle(cnv_x, cnv_y, cnv_w, cnv_h));
		}
	}

	kocka[] kocky;

	Label skore;
	Label skore_d;
	Label level;
	Label level_d;
	Button start;

	Label dbg;

	int sq_w = 10; // velkost kocky
	int cnv_x = 140;
	int cnv_y = 40;
	int cnv_h = sq_w * 40; // vyska hracej plochy
	int cnv_w = sq_w * 10; // sirka hracej plochy

	public void init()
	{
		setLayout(null);

		kocky = null;

		dbg     = new Label("debug info");
		skore_d = new Label("SKORE");
		skore   = new Label("0");
		level_d = new Label("Level");
		level   = new Label("1");

		start   = new Button("START");

		dbg.setBounds(    20,  1, 100, 20);
		skore_d.setBounds(20, 20, 100, 20);
		skore.setBounds(  20, 40, 100, 20);
		level_d.setBounds(20, 60, 100, 20);
		level.setBounds(  20, 80, 100, 20);

		//start.setBounds(300,300,100,50);
		start.setBounds(cnv_x, cnv_y, start.getWidth(), start.getHeight());

		add(skore_d);
		add(skore);
		add(level_d);
		add(level);
		add(start);

		/*
		nul.addActionListener(this);
		vyt.addActionListener(this);
		*/
	}

	public void paint (Graphics gX)
	{
		Graphics2D g = (Graphics2D) gX;

		g.setColor (Color.red);
		g.draw(new Rectangle(cnv_x, cnv_y, cnv_w, cnv_h));
		//g2D.fill (spanok);
	}

	public void keyReleased(KeyEvent evt) {
	}

	public void keyPressed(KeyEvent evt) {
	}

	public void keyTyped(KeyEvent evt) {
	}


	/*
	public void zmen()
	{
		hs = pss.getValue();
		psc.setText(String.valueOf(hs));

		hp = pps.getValue();
		ppc.setText(String.valueOf(hp));

		ho = pos.getValue();
		poc.setText(String.valueOf(ho));

		hu = pus.getValue();
		puc.setText(String.valueOf(hu));

		spolu = hs + hp + ho + hu;
		spc.setText(String.valueOf(spolu));

		spanok = new Arc2D.Double (500.,  30., 250., 250., 0                , sp, Arc2D.PIE);
		praca  = new Arc2D.Double (500.,  30., 250., 250., sp               , pr, Arc2D.PIE);
		oddych = new Arc2D.Double (500.,  30., 250., 250., sp + pr          , od, Arc2D.PIE);
		ucenie = new Arc2D.Double (500.,  30., 250., 250., sp + pr + od     , uc, Arc2D.PIE);
		nic    = new Arc2D.Double (500.,  30., 250., 250., sp + pr + od + uc, ni, Arc2D.PIE);

		repaint();
	}

	public void nuluj()
	{
		// nastavim vsetky scrollbary na 0 a tvarim sa akoby niekto klikol na scrollbar
		pss.setValue(0);
		pps.setValue(0);
		pos.setValue(0);
		pus.setValue(0);
	}

	// toto odchytava vsetky posuvania scrollbarov
	public void adjustmentValueChanged(AdjustmentEvent evt)
	{
		// telo tejto funkcie si odlozim samostatne - budem ju volat aj odinakadial
		zmen();
	}

	// toto odchytava klikanie tlacitok
	public void actionPerformed(ActionEvent evt)
	{
		Object src = evt.getSource();
		if (src == nul) {
			nuluj(); // ak to bolo nulovacie tlacitko nastavim vsade nuly
		}
		// potom necham spracovat zmeny scrollbarov a vykreslit
		zmen();
	}
	*/
}

