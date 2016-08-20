package marf.Preprocessing.Endpoint;

import java.io.Serializable;
import java.util.Vector;

import marf.MARF;
import marf.Preprocessing.IPreprocessing;
import marf.Preprocessing.Preprocessing;
import marf.Preprocessing.PreprocessingException;
import marf.Storage.ModuleParams;
import marf.Storage.Sample;
import marf.util.Arrays;


/**
 * <p>Implements endpoint preprocessing in MARF.</p>
 *
 * <p>Endpoints are considered to be the local minimum
 * and maximum values of an amplitude. A few variations
 * apply.</p>
 *
 * @author Serguei Mokhov
 */
public class Endpoint
extends Preprocessing
{
	/**
	 * Indicates whether to compress or not the sample
	 * before extracting edpoints. Default is <code>false</code>.
	 * @since 0.3.0.5
	 */
	protected boolean bCompress = false;
	
	/**
	 * Indicates whether to consider or or not the edge points
	 * (at the beginning and the end of the sample) as endpoints.
	 * Default is <code>true</code>.
	 * @since 0.3.0.5
	 */
	protected boolean bConsiderEdges = true;
	
	/**
	 * For serialization versioning.
	 * When adding new members or make other structural
	 * changes regenerate this number with the
	 * <code>serialver</code> tool that comes with JDK.
	 */
	private static final long serialVersionUID = -3032497180742434272L;

	/**
	 * Default constructor for reflective creation of Preprocessing
	 * clones. Typically should not be used unless really necessary
	 * for the frameworked modules.
	 * @since 0.3.0.5
	 */
	public Endpoint()
	{
		super();
	}

	/**
	 * Preprocessing pipeline constructor.
	 * @param poPreprocessing follow-up preprocessing module
	 * @throws PreprocessingException
	 * @since 0.3.0.3
	 */
	public Endpoint(IPreprocessing poPreprocessing)
	throws PreprocessingException
	{
		super(poPreprocessing);
		processModuleParams();
	}

	/**
	 * Endpoint Constructor.
	 * @param poSample incoming sample
	 * @throws PreprocessingException
	 */
	public Endpoint(Sample poSample)
	throws PreprocessingException
	{
		super(poSample);
		processModuleParams();
	}

	/**
	 * Filters out sample points that are not endpoints.
	 * In general case, the endpoints are local minimums
	 * and maximums, edges, and continuous equality points.
	 * The latter two can be optionally eliminated if desired.
	 * First got implemented in 0.3.0.5.
	 *
	 * @throws PreprocessingException if the resulting endpoints array contains no data
	 * @return <code>true</code> if the preprocessing was successful.
	 * @see #bCompress
	 * @see #bConsiderEdges
	 */
	public boolean preprocess()
	throws PreprocessingException
	{
		boolean bRetVal = super.preprocess();
		
		// Apply compression
		if(this.bCompress == true)
		{
			bRetVal &= compress();
		}

		double[] adSampleRef = this.oSample.getSampleArray();
		
		// Assume the maximum length, and trim later.
		double[] adTempSample = new double[adSampleRef.length];
		
		// Index, pointing to the last element after which
		// stuff has to be cut off.
		int iLastIndex = -1;
		
		for(int i = 0; i < adSampleRef.length; i++)
		{
			if
			(
				// Start and end
				((i == 0 || i == adSampleRef.length - 1) && this.bConsiderEdges == true)
				
				// Local maximum
				|| (adSampleRef[i - 1] < adSampleRef[i] &&  adSampleRef[i + 1] < adSampleRef[i])

				// Local minimum
				|| (adSampleRef[i - 1] > adSampleRef[i] &&  adSampleRef[i + 1] > adSampleRef[i])

				// Continuous equality
				|| (adSampleRef[i - 1] == adSampleRef[i] ||  adSampleRef[i + 1] == adSampleRef[i])
			)
			{
				adTempSample[++iLastIndex] = adSampleRef[i];
			}
		}

		if(iLastIndex < 0)
		{
			throw new PreprocessingException("The endpoints array has no data.");
		}
		
		// Care only for endpoints; trim the rest
		double[] adEndpoints = new double[iLastIndex + 1];
		Arrays.copy(adEndpoints, 0, adTempSample, 0, adEndpoints.length);

		// Put it back
		this.oSample.setSampleArray(adEndpoints);
		
		return bRetVal;
	}

	/**
	 * Enables or disables sample compression prior
	 * endpoints extraction.
	 *
	 * @param pbEnable <code>true</code> if the compression is desired
	 * @return the old value of the compression flag
	 * @since 0.3.0.5
	 */
	public boolean enableCompression(boolean pbEnable)
	{
		boolean bOldValue = this.bCompress;
		this.bCompress = pbEnable;
		return bOldValue;
	}
	
	/**
	 * Enables or disables consideration of the sample edges
	 * as endpoints.
	 *  
	 * @param pbEnable <code>true</code> if the consideration is desired
	 * @return the old value of the flag
	 * @since 0.3.0.5
	 */
	public boolean enableEdgeEndpoints(boolean pbEnable)
	{
		boolean bOldValue = this.bConsiderEdges;
		this.bConsiderEdges = pbEnable;
		return bOldValue;
	}

	/**
	 * A common local routine for extraction endpointing
	 * parameters via the ModuleParams machinery.
	 * 
	 * @since 0.3.0.5
	 */
	protected void processModuleParams()
	{
		ModuleParams oModuleParams = MARF.getModuleParams();

		if(oModuleParams != null)
		{
			Vector<Serializable> oEndpointParams = oModuleParams.getPreprocessingParams();

			// First three in the Preprocessing protocol are noise, silence, and
			// silence threshold.
			// TODO: somehow remove/abstract the hardcoded values of 5, 3, and 4
			if(oEndpointParams != null && oEndpointParams.size() >= 5)
			{
				enableCompression(((Boolean)oEndpointParams.elementAt(3)).booleanValue());
				enableEdgeEndpoints(((Boolean)oEndpointParams.elementAt(4)).booleanValue());
			}
		}
	}
}

// EOF
