package org.decampo.aws.glacier;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.UploadResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class UploadArchive
{
    public static void main(String[] args) throws Exception
    {
        int result = -1;
        try
        {
            final File archive = new File(args[0]);
            result = new UploadArchive().upload(archive);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            System.exit(result);
        }
    }
    
    public int upload(final File archive) 
        throws IOException, AmazonClientException 
    {
        final long start = System.currentTimeMillis();
        
        if (!archive.exists())
        {
            throw new FileNotFoundException(archive.getName());
        }
        if (archive.isDirectory())
        {
            throw new IOException(archive + " is a directory.");
        }
        if (!archive.canRead())
        {
            throw new IOException(archive + " cannot be read.");
        }
        
        final VaultDetails details = new VaultDetails();
        
        final AmazonGlacierClient client = new AmazonGlacierClient(details);
        client.setEndpoint("https://glacier." + details.region + ".amazonaws.com");

        final String archivePath = archive.getAbsolutePath();
        final ArchiveTransferManager atm = 
            new ArchiveTransferManager(client, details); 
        final UploadResult result = atm.upload(
            "-", details.vaultName, archivePath, archive, new ProgressLogger());
        System.out.println(archivePath + ":" + result.getArchiveId());

        final long elapsed = (System.currentTimeMillis() - start) / 1000;
        System.err.println("Upload time: " 
            + (elapsed/3600) + ':' + ((elapsed/60)%60) + ':' + (elapsed%60));
        
        return 0;
    }
}
