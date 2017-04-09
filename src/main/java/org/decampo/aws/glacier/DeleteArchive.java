package org.decampo.aws.glacier;

import java.io.IOException;

import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.DeleteArchiveRequest;

import com.amazonaws.AmazonClientException;

public class DeleteArchive
{
    public static void main(String[] args) throws Exception
    {
        int result = -1;
        try
        {
            final String archiveId = args[0];
            result = new DeleteArchive().delete(archiveId);
        }
        finally
        {
            System.exit(result);
        }
    }
    
    public int delete(final String archiveId)
        throws IOException, AmazonClientException 
    {
        final VaultDetails details = new VaultDetails();
        
        final AmazonGlacierClient client = new AmazonGlacierClient(details);
        client.setEndpoint("https://glacier." + details.region + ".amazonaws.com");

        client.deleteArchive(new DeleteArchiveRequest()
            .withVaultName(details.vaultName)
            .withArchiveId(archiveId)); 
        
        return 0;
    }
}
