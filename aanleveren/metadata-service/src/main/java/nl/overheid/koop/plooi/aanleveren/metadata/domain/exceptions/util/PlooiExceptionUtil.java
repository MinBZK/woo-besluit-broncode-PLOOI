package nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.util;

import com.fasterxml.jackson.core.JsonPointer;
import com.networknt.schema.ValidationMessage;

import java.util.List;

import static nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions.ExceptionConstants.ERROR_MESSAGE_NOT_IN_ENUMERATION;

/*PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP*/
public class PlooiExceptionUtil {

    private PlooiExceptionUtil() {
    }

    public static JsonPointer generateSchemaPointer(final ValidationMessage msg) {
        var path = msg.getMessage().substring(0, msg.getMessage().indexOf(":"));
        var pathStr = path.replace("$.", "/");
        pathStr = pathStr.replace(".", "/");
        pathStr = pathStr.replace("[", "/");
        pathStr = pathStr.replace("]", "");  // array closure superfluous
        return JsonPointer.valueOf(pathStr);
    }

    public static JsonPointer generateDataPointer(final ValidationMessage msg) {
        var pathStr = msg.getPath().replace("$.", "/");
        if (pathStr.equals("$")) { // in case of root
            pathStr = "/";
        }
        pathStr = pathStr.replace(".", "/");
        pathStr = pathStr.replace("[", "/");
        pathStr = pathStr.replace("]", "");  // array closure superfluous
        return JsonPointer.valueOf(pathStr);
    }

    public static String generateContentForPlooiJsonParseException(final String msg) {
        var errors = splitErrors(msg);
        var content = new StringBuilder();
        for (var error : errors) {
            if (!content.toString().isEmpty()) {
                content.append("  |  ");
            }
            error = error.replace("$.", "");
            content.append(error);
        }
        return content.toString();
    }

    public static String[] splitErrors(final String errors) {
        return errors.split("\n");
    }

    public static String generateContentForYAMLException(final Exception e) {

        var message = e.getCause() != null ? e.getCause().getLocalizedMessage() : e.getLocalizedMessage();
        if (message.contains("\n")) {
            message = message.substring(0, message.indexOf("\n"));
            return message;
        }

        return message;
    }

    public static String editValidationMessage(final String validiationMessage) {
        var content = validiationMessage;
        var pos = validiationMessage.indexOf(ERROR_MESSAGE_NOT_IN_ENUMERATION);
        if (pos > -1) {
            content = validiationMessage.substring(0, pos + ERROR_MESSAGE_NOT_IN_ENUMERATION.length());
        }

        return content;
    }

    public static String contentToString(final List<String> contentMessages) {
        var content = new StringBuilder();

        for (var contentMessage : contentMessages) {
            if (content.length() > 0) {
                content.append("\n");
            }
            content.append(contentMessage);
        }

        return content.toString();
    }
}
