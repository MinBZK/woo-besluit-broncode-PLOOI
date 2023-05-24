package nl.overheid.koop.plooi.aanleveren.metadata.domain.exceptions;

/*PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP*/
public class PlooiJsonParseException extends BadRequestException {

    public PlooiJsonParseException(final String message) {
        super(message);
    }

    public PlooiJsonParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
