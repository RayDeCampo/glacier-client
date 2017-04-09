package org.decampo.aws.glacier;

import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.InitiateJobRequest;
import com.amazonaws.services.glacier.model.InitiateJobResult;
import com.amazonaws.services.glacier.model.JobParameters;

public class VaultInventory
{
    public static void main(String[] args)
    {
        final VaultDetails details = new VaultDetails();
        
        final AmazonGlacierClient client = new AmazonGlacierClient(details);
        client.setEndpoint("https://glacier." + details.region + ".amazonaws.com");
        
        final InitiateJobRequest request = new InitiateJobRequest()
            .withVaultName(details.vaultName)
            .withJobParameters(
                new JobParameters()
                    .withType("inventory-retrieval")
                    .withSNSTopic(details.topicArn)
                );
        
        final InitiateJobResult response = client.initiateJob(request);
        System.out.println(response.getJobId());

    }
}
