package org.decampo.aws.glacier;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.GetJobOutputRequest;
import com.amazonaws.services.glacier.model.GetJobOutputResult;

public class VaultInventoryResult
{
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args)
    {
        final String jobId = args[0];
        
        final VaultDetails details = new VaultDetails();
        
        final AmazonGlacierClient client = new AmazonGlacierClient(details);
        client.setEndpoint("https://glacier." + details.region + ".amazonaws.com");
        
        final GetJobOutputRequest request = new GetJobOutputRequest()
            .withVaultName(details.vaultName)
            .withJobId(jobId);
        
        final GetJobOutputResult getJobOutputResult = 
            client.getJobOutput(request);
        try (final InputStream in = getJobOutputResult.getBody();
            final OutputStream out = new BufferedOutputStream(
                new FileOutputStream("inventory.dat")))
        {
            @SuppressWarnings("UnusedAssignment")
            int b = -1;
            while ((b = in.read()) != -1)
            {
                out.write(b);
            }
        }
        catch (final IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}
