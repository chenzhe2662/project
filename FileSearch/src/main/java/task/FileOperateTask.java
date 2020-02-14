package task;

import app.FileMeta;
import dao.FileOperatorDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileOperateTask implements FileScanCallback {

    @Override
    public void execute(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();

            List<FileMeta> localMetas = compose(children);

            List<FileMeta> metas = FileOperatorDAO.query(dir.getPath());

            for (FileMeta meta : metas) {
                if (!localMetas.contains(meta)) {
                    FileOperatorDAO.delete(meta);
                }
            }

            for (FileMeta localMeta : localMetas) {
                if (!metas.contains(localMeta)) {
                    FileOperatorDAO.insert(localMeta);
                }
            }


        }

    }

    private List<FileMeta> compose(File[] children) {
        List<FileMeta> metas = new ArrayList<>();
        if (children != null) {
            for (File child : children) {
                metas.add(new FileMeta(child));
            }
        }
        return metas;
    }
}
