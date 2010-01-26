package org.sonatype.flexmojos.asdoc;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Goal which generates documentation from the ActionScript sources in DITA format.
 * 
 * @phase package
 * @goal attach-asdoc
 * @requiresDependencyResolution
 */
public class AttachAsdocMojo
    extends AsDocMojo
{

    /**
     * The filename of bundled asdoc
     * 
     * @parameter default-value="${project.build.directory}/${project.build.finalName}-asdoc.zip"
     */
    private File output;

    /**
     * @component
     */
    private ArchiverManager archiverManager;

    /**
     * @component
     */
    protected MavenProjectHelper projectHelper;

    @Override
    protected void tearDown()
        throws MojoExecutionException, MojoFailureException
    {
        super.tearDown();

        output.getParentFile().mkdirs();

        Archiver archiver;
        try
        {
            archiver = archiverManager.getArchiver( output );
        }
        catch ( NoSuchArchiverException e )
        {
            throw new MojoExecutionException( "Invalid file type", e );
        }
        try
        {
            archiver.addDirectory( outputDirectory );
            archiver.setDestFile( output );
            archiver.createArchive();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Failed to create bundle", e );
        }

        projectHelper.attachArtifact( project, getFileExtention( output ), "asdoc", output );
    }

    private static String getFileExtention( File file )
    {
        String path = file.getAbsolutePath();

        String archiveExt = FileUtils.getExtension( path ).toLowerCase();

        if ( "gz".equals( archiveExt ) || "bz2".equals( archiveExt ) )
        {
            String[] tokens = StringUtils.split( path, "." );

            if ( tokens.length > 2 && "tar".equals( tokens[tokens.length - 2].toLowerCase() ) )
            {
                archiveExt = "tar." + archiveExt;
            }
        }

        return archiveExt;
    }

}
