package info.touret.googledriveclient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by touret-a on 22/05/2015.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LocalFileHelper.class})
public class LocalFileHelperTest {

    private LocalFileHelper localFileHelper;
    @Mock
    private Path mockRootPath;

    @Mock
    private FileSystem mockFileSystem;

    @Before
    public void setUp() throws Exception {
        mockRootPath = PowerMockito.mock(Path.class);
        PowerMockito.mockStatic(Files.class);

        localFileHelper = new LocalFileHelper();

    }

    @Test
    public void testlistRecenFilesFromAFolder_Empty() throws Exception {
        PowerMockito.when(Files.list(mockRootPath)).thenReturn(new ArrayList<Path>().stream());
        List<File> fileList = localFileHelper.listRecentFilesFromAFolder(mockRootPath, Instant.now());
        assertTrue(fileList.isEmpty());
    }


    @Test
    public void testlistRecenFilesFromAFolder_Not_Empty() throws Exception {
        /* Definition des mocks*/
        Path mockPath1 = PowerMockito.mock(Path.class);
        Path mockPath2 = PowerMockito.mock(Path.class);
        PowerMockito.when(mockPath1.toFile()).thenReturn(PowerMockito.mock(File.class));
        PowerMockito.when(mockPath2.toFile()).thenReturn(PowerMockito.mock(File.class));
        PowerMockito.when(mockPath1.toFile().lastModified()).thenReturn(Instant.now().getEpochSecond());
        PowerMockito.when(mockPath2.toFile().lastModified()).thenReturn(Instant.MIN.getEpochSecond());

        List<Path> files = new ArrayList<>();
        Collections.addAll(files, mockPath1, mockPath2);
        PowerMockito.when(Files.list(mockRootPath)).thenReturn(files.stream());
        /* Execution de la methode */
        List<File> fileList = localFileHelper.listRecentFilesFromAFolder(mockRootPath, Instant.EPOCH);
        assertTrue(!fileList.isEmpty());
        assertEquals(1, fileList.size());
    }

    @Test
    public void testCreateOrGetFolder_With_Folder_Creation_OK() throws Exception {
        Path mockPath1 = PowerMockito.mock(Path.class);
        String mockDir = "test";
        Path newPath = localFileHelper.createOrGetFolder(mockPath1, mockDir);
        assertNotNull(newPath);
        assertTrue(newPath.endsWith(mockDir));
    }

    @Test
    public void testCreateOrGetFolder_Without_Folder_Creation_OK() throws Exception {
        Path mockPath1 = PowerMockito.mock(Path.class);
        String mockDir = "test";
        PowerMockito.when(Files.exists(Matchers.any())).thenReturn(true);
        Path newPath = localFileHelper.createOrGetFolder(mockPath1, mockDir);
        assertNotNull(newPath);
        assertTrue(newPath.endsWith(mockDir));
    }

    @Test
    public void testCreateOrGetFolder_With_Folder_Creation_KO() throws Exception {
        try {
            Path mockPath1 = PowerMockito.mock(Path.class);
            String mockDir = "test";
            PowerMockito.when(Files.exists(Matchers.any())).thenReturn(false);
            PowerMockito.when(Paths.get(mockPath1.toString(), mockDir)).thenThrow(new InvalidPathException("NULL", "NULL"));
            Path newPath = localFileHelper.createOrGetFolder(mockPath1, mockDir);
            fail("An InvalidPathException must be thrown");
        } catch (InvalidPathException e) {
            //OK
        }


    }
}
