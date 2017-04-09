package org.decampo.aws.glacier;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManagerBuilder;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import java.io.File;
import java.io.IOException;

public class DownloadArchive {
    public static void main(String[] args) throws Exception {
        int result = -1;

        try {
            result = new DownloadArchive().download(args[0], new File(args[1]));
        } catch (final Throwable t) {
            t.printStackTrace();
        } finally {
            System.exit(result);
        }

    }

    public int download(final String archiveId, final File destination)
        throws IOException {
        final long start = System.currentTimeMillis();

        final VaultDetails details = new VaultDetails();
        final AWSStaticCredentialsProvider credentials =
            new AWSStaticCredentialsProvider(details);

        final AmazonGlacierClient client = (AmazonGlacierClient)
            AmazonGlacierClientBuilder.standard()
                .withCredentials(credentials)
                .withRegion(details.region)
                .build();

        final AmazonSQSClient sqs = (AmazonSQSClient)
            AmazonSQSClientBuilder.standard()
                .withCredentials(credentials)
                .withRegion(details.region)
                .build();

        final AmazonSNSClient sns = (AmazonSNSClient)
            AmazonSNSClientBuilder.standard()
                .withCredentials(credentials)
                .withRegion(details.region)
                .build();

        final ArchiveTransferManager atm = new ArchiveTransferManagerBuilder()
            .withGlacierClient(client)
            .withSnsClient(sns)
            .withSqsClient(sqs)
            .build();

        atm.download("-", details.vaultName, archiveId, destination, new ProgressLogger());

        final long elapsed = (System.currentTimeMillis() - start) / 1000;
        System.err.println("Download time: "
                + (elapsed/3600) + ':' + ((elapsed/60)%60) + ':' + (elapsed%60));
        return 0;
    }
}
