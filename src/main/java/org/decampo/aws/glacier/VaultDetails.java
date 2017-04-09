package org.decampo.aws.glacier;

import com.amazonaws.auth.AWSCredentials;
import java.util.ResourceBundle;

public class VaultDetails implements AWSCredentials
{
    protected String accountId;
    protected String accessKey;
    protected String secretKey;
    protected String vaultName;
    protected String region;
    protected String topicArn;

    public VaultDetails()
    {
        final ResourceBundle props = ResourceBundle.getBundle("glacier-client");
        accountId = props.getString("vault.details.account.id");
        accessKey = props.getString("vault.details.access.key");
        secretKey = props.getString("vault.details.secret.key");
        vaultName = props.getString("vault.details.vault.name");
        region = props.getString("vault.details.region");
        topicArn = props.getString("vault.details.topic.ARN");
    }

    @Override
    public String getAWSAccessKeyId()
    {
        return accessKey;
    }
    
    @Override
    public String getAWSSecretKey()
    {
        return secretKey;
    }
}
