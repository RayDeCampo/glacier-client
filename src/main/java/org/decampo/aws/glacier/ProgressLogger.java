package org.decampo.aws.glacier;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ProgressLogger implements ProgressListener
{
    private final long start = System.currentTimeMillis();
    private long totalBytes = 0;

    @Override
    public void progressChanged(ProgressEvent progressEvent)
    {
        final String elapsed = determineElapsed();
        final String event = determineEvent(progressEvent);
        final String bytes = determineBytes(progressEvent);
        
        System.err.printf("[%s] %s%s%n", elapsed, event, bytes);
    }

    private String determineElapsed()
    {
        final long now = System.currentTimeMillis();

        final long elapsed = (now - start) / 1000;
        final long seconds = elapsed % 60;
        final long minutes = (elapsed / 60) % 60;
        final long hours = elapsed / 60 / 60;
        
        final StringWriter result = new StringWriter();
        final PrintWriter printer = new PrintWriter(result);
        printer.printf("%02d:%02d:%02d", hours, minutes, seconds);
        return result.toString();
    }

    private String determineEvent(ProgressEvent progressEvent)
    {
        final ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType)
        {
            case REQUEST_CONTENT_LENGTH_EVENT:
            case RESPONSE_CONTENT_LENGTH_EVENT:
                return "length";

            case REQUEST_BYTE_TRANSFER_EVENT:
            case RESPONSE_BYTE_TRANSFER_EVENT:
                return "transfer";

            case RESPONSE_BYTE_DISCARD_EVENT:
                return "discard";

            case CLIENT_REQUEST_STARTED_EVENT:
                return "request started";

            case HTTP_REQUEST_STARTED_EVENT:
                return "HTTP request started";

            case HTTP_REQUEST_COMPLETED_EVENT:
                return "HTTP request completed";

            case HTTP_REQUEST_CONTENT_RESET_EVENT:
                return "HTTP request reset";

            case CLIENT_REQUEST_RETRY_EVENT:
                return "retry";

            case HTTP_RESPONSE_STARTED_EVENT:
                return "HTTP response started";

            case HTTP_RESPONSE_COMPLETED_EVENT:
                return "HTTP response completed";

            case HTTP_RESPONSE_CONTENT_RESET_EVENT:
                return "HTTP response reset";

            case CLIENT_REQUEST_SUCCESS_EVENT:
                return "success";

            case CLIENT_REQUEST_FAILED_EVENT:
            case TRANSFER_FAILED_EVENT:
                return "failed";

            case TRANSFER_CANCELED_EVENT:
                return "canceled";
                
            case TRANSFER_COMPLETED_EVENT:
                return "completed";
                
            case TRANSFER_PART_COMPLETED_EVENT:
                return "part completed";
                
            case TRANSFER_PART_FAILED_EVENT:
                return "part failed";
                
            case TRANSFER_PART_STARTED_EVENT:
                return "part started";
                
            case TRANSFER_PREPARING_EVENT:
                return "preparing";
                
            case TRANSFER_STARTED_EVENT:
                return "started";
                
            default:
                return "unknown";
        }
    }

    private String determineBytes(ProgressEvent progressEvent)
    {
        final long bytes = progressEvent.getBytesTransferred();
        if (bytes > 0)
        {
            totalBytes += bytes;
            final String transferred = formatBytes(bytes);
            final String total = formatBytes(totalBytes);
            final StringWriter result = new StringWriter();
            final PrintWriter printer = new PrintWriter(result);
            printer.printf(": %s (%s total)", transferred, total);
            return result.toString();
        }
        else
        {
            return "";
        }
    }
    
    private String formatBytes(final long bytes)
    {
        final String[] units = {"bytes", "KB", "MB", "GB"};
        double amount = bytes;
        String unit = null;
        for (final String candidate : units)
        {
            if (amount <= 1024)
            {
                unit = candidate;
                break;
            }
            else if (candidate.equals(units[units.length-1]))
            {
                unit = candidate;
                break;
            }
            else 
            {
                amount = amount/1024;
            }
        }
        final StringWriter result = new StringWriter();
        final PrintWriter printer = new PrintWriter(result);
        printer.printf("%1.3f %s", amount, unit);
        return result.toString();
    }
}
