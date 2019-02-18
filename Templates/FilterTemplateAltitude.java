/******************************************************************************************************************
* File:FilterTemplate.java
* Course: 17655
* Project: Assignment 1
* Copyright: Copyright (c) 2003 Carnegie Mellon University
* Versions:
*	1.0 November 2008 - Initial rewrite of original assignment 1 (ajl).
*
* Description:
*
* This class serves as a template for creating filters. The details of threading, filter connections, input, and output
* are contained in the FilterFramework super class. In order to use this template the program should rename the class.
* The template includes the run() method which is executed when the filter is started.
* The run() method is the guts of the filter and is where the programmer should put their filter specific code.
* In the template there is a main read-write loop for reading from the input port of the filter and writing to the
* output port of the filter. This template assumes that the filter is a "normal" that it both reads and writes data.
* That is both the input and output ports are used - its input port is connected to a pipe from an up-stream filter and
* its output port is connected to a pipe to a down-stream filter. In cases where the filter is a source or sink, you
* should use the SourceFilterTemplate.java or SinkFilterTemplate.java as a starting point for creating source or sink
* filters.
*
* Parameters: 		None
*
* Internal Methods:
*
*	public void run() - this method must be overridden by this class.
*
******************************************************************************************************************/
import java.util.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class FilterTemplateAltitude extends FilterFramework
{

	

	public void run()
    {
    	int bytesread = 0;					// Number of bytes read from the input file.
    	int bytescounter = 0;
		int byteswritten = 0;				// Number of bytes written to the stream.
		byte databyte = 0;
		long measurement = 0;
		int i;
		long getByte;
		double result=0;
		double metersConvMultiplier = 3048;
		double metersConvDivider = 10000;

		while (true)
		{

/***************************************************************
*	The program can insert code for the filter operations
* 	here. Note that data must be received and sent one
* 	byte at a time. This has been done to adhere to the
* 	pipe and filter paradigm and provide a high degree of
* 	portabilty between filters. However, you must reconstruct
* 	data on your own. First we read a byte from the input
* 	stream...
***************************************************************/

			try
			{
				databyte = ReadFilterInputPort();
				bytesread++;
				bytescounter++;
				getByte=8;
				//System.out.println("mmmmmmwmwmwmwmwmwmwmwmwmwmwmwwmwmwm:    "+bytescounter);
				if(bytescounter==29){
					
					measurement = 0;

					for (i=0; i<8; i++ )
					{	
						if(i!=0){
							//System.out.println(i);
							databyte = ReadFilterInputPort();
							bytesread++;
							bytescounter++;
						}
						measurement = measurement | (databyte & 0xFF);	// We append the byte on to measurement...
						//System.out.print("CHECK: "+measurement);
						if (i != 8-1)					// If this is not the last byte, then slide the
						{												// previously appended byte to the left by one byte
							measurement = measurement << 8;				// to make room for the next byte we append to the
																		// measurement
						} // if
						//System.out.print("\n");
															// Increment the byte count

					} // if

					result = Double.longBitsToDouble(measurement) * metersConvMultiplier;
					result = result / metersConvDivider;
					byte [] bytes = ByteBuffer.allocate(8).putDouble(result).array();
					
					for (i=0; i<8; i++ )
					{
						WriteFilterOutputPort(bytes[i]);
           				byteswritten++;
           			}


				}else{

					WriteFilterOutputPort(databyte);
           			byteswritten++;
				}
           		
           		

			} // try

/***************************************************************
*	When we reach the end of the input stream, an exception is
* 	thrown which is shown below. At this point, you should
* 	finish up any processing, close your ports and exit.
***************************************************************/

			catch (EndOfStreamException e)
			{
				ClosePorts();
				System.out.print( "\n" + this.getName() + "::Middle Exiting; bytes read: " + bytesread + " bytes written: " + byteswritten );
				break;

			} // catch
			if(bytescounter==60){
           			bytescounter=0;
           	}

		} // while

   } // run

} // FilterTemplate