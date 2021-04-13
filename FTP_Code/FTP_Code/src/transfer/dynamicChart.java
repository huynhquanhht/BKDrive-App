package transfer;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

public class dynamicChart extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4636550077401306481L;
	/** The time series data. */
	private TimeSeries series;
	private double lastValue = 0;
	private String title;

	@SuppressWarnings("deprecation")
	public dynamicChart(final String title) {
		super(title);
		this.title = title;

		this.series = new TimeSeries("", Millisecond.class);
		final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
		final JFreeChart chart = createChart(dataset);

		final ChartPanel chartPanel = new ChartPanel(chart);

		final JPanel content = new JPanel(new BorderLayout());
		content.add(chartPanel);

		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(content);

	}

	private JFreeChart createChart(final XYDataset dataset) {
		final JFreeChart result = ChartFactory.createTimeSeriesChart(this.title, "Time", "Value", dataset, true, true,
				false);
		final XYPlot plot = result.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(60000.0); // 60 seconds
		axis = plot.getRangeAxis();
		axis.setRange(0.0, 100.0);
		return result;
	}

	public void addNewDataHeap() {
		long heapSize = Runtime.getRuntime().totalMemory();
		new File("heap.txt");

		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Get current size of heap in bytes

			// Get maximum size of heap in bytes. The heap cannot grow beyond this size.//
			// Any attempt will result in an OutOfMemoryException.
//			long heapMaxSize = Runtime.getRuntime().maxMemory();

			// Get amount of free memory within the heap in bytes. This size will increase
			// // after garbage collection and decrease as new objects are created.
			long heapFreeSize = Runtime.getRuntime().freeMemory();
			this.lastValue = (1 - heapFreeSize * 1.0 / heapSize) * 100.0;
//			System.out.println(this.lastValue);
			this.series.add(new Millisecond(), this.lastValue);

//			FileWriter myWriter = null;
//			try {
//				myWriter = new FileWriter("heap.txt");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			try {
//
//				myWriter.write(Double.toString(this.lastValue) + "\n");
//				myWriter.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
		}
	}
}

//	public static void main(final String[] args) {
//
//		final dynamicChart demo = new 
//				dynamicChart("Dynamic Data Demo");
//		demo.pack();
//		RefineryUtilities.centerFrameOnScreen(demo);
//		demo.setVisible(true);
//
//	}
//}
//import java.awt.BorderLayout;
//import javax.swing.JPanel;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.axis.ValueAxis;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.data.time.Millisecond;
//import org.jfree.data.time.TimeSeries;
//import org.jfree.data.time.TimeSeriesCollection;
//import org.jfree.data.xy.XYDataset;
//import org.jfree.ui.ApplicationFrame;
//
//public class dynamicChart extends ApplicationFrame {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -4038520485509297828L;
//	private TimeSeries series;
//
//	private JFreeChart createChart(final XYDataset dataset) {
//		final JFreeChart result = ChartFactory.createTimeSeriesChart("Heap value", "Time", "Value", dataset,
//				true, true, false);
//		final XYPlot plot = result.getXYPlot();
//		ValueAxis axis = plot.getDomainAxis();
//		axis.setAutoRange(true);
//		axis.setFixedAutoRange(60000.0); // 60 seconds
//		axis = plot.getRangeAxis();
//		axis.setRange(0.0, 200.0);
//		
//		return result;
//	}
//
//	public void addNewData() {
//
//		// Get maximum size of heap in bytes. The heap cannot grow beyond this size.//
//		// Any attempt will result in an OutOfMemoryException.
//		long heapMaxSize = Runtime.getRuntime().maxMemory();
//
////					System.out.println("heapsize: "+heapSize + " heapmaxsize: "+ heapMaxSize+ " heapFreeSize: "+heapFreeSize);
//
//		while (true) {
//			// Get amount of free memory within the heap in bytes. This size will increase
//			// after garbage collection and decrease as new objects are created.
//			// long heapFreeSize = Runtime.getRuntime().freeMemory();
//			long heapSize = Runtime.getRuntime().totalMemory();
////			final Millisecond now = new Millisecond();
//			this.series.add(new Millisecond(), heapSize/heapMaxSize*100);
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public dynamicChart(String title) {
//		super(title);
//		//this.series = new TimeSeries("Random Data", Millisecond.class);
////		final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
//		final JFreeChart chart = createChart(null);
//		
//		
//		
//		final ChartPanel chartPanel = new ChartPanel(chart);
//		final JPanel content = new JPanel(new BorderLayout());
//		content.add(chartPanel);
//		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//
//		setContentPane(content);
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//			addNewData();
//				
//			}
//		}).start();
//
//	}
//
//}
