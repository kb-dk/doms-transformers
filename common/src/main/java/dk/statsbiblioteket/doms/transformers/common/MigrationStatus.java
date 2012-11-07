package dk.statsbiblioteket.doms.transformers.common;

/**
 * Status code returned by transformations/migrations.
 */
public enum MigrationStatus {
    COMPLETE, INCOMPLETE, FAILED, IGNORED;
}
