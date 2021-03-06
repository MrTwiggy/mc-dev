package net.minecraft.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

public abstract class StructureGenerator extends WorldGenBase {

    protected Map d = new HashMap();

    public StructureGenerator() {}

    protected void a(World world, int i, int j, int k, int l, byte[] abyte) {
        if (!this.d.containsKey(Long.valueOf(ChunkCoordIntPair.a(i, j)))) {
            this.b.nextInt();

            try {
                if (this.a(i, j)) {
                    StructureStart structurestart = this.b(i, j);

                    this.d.put(Long.valueOf(ChunkCoordIntPair.a(i, j)), structurestart);
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Exception preparing structure feature");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Feature being prepared");

                crashreportsystemdetails.a("Is feature chunk", (Callable) (new CrashReportIsFeatureChunk(this, i, j)));
                crashreportsystemdetails.a("Chunk location", String.format("%d,%d", new Object[] { Integer.valueOf(i), Integer.valueOf(j)}));
                crashreportsystemdetails.a("Chunk pos hash", (Callable) (new CrashReportChunkPosHash(this, i, j)));
                crashreportsystemdetails.a("Structure type", (Callable) (new CrashReportStructureType(this)));
                throw new ReportedException(crashreport);
            }
        }
    }

    public boolean a(World world, Random random, int i, int j) {
        int k = (i << 4) + 8;
        int l = (j << 4) + 8;
        boolean flag = false;
        Iterator iterator = this.d.values().iterator();

        while (iterator.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator.next();

            if (structurestart.d() && structurestart.a().a(k, l, k + 15, l + 15)) {
                structurestart.a(world, random, new StructureBoundingBox(k, l, k + 15, l + 15));
                flag = true;
            }
        }

        return flag;
    }

    public boolean a(int i, int j, int k) {
        Iterator iterator = this.d.values().iterator();

        while (iterator.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator.next();

            if (structurestart.d() && structurestart.a().a(i, k, i, k)) {
                Iterator iterator1 = structurestart.b().iterator();

                while (iterator1.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator1.next();

                    if (structurepiece.b().b(i, j, k)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public ChunkPosition getNearestGeneratedFeature(World world, int i, int j, int k) {
        this.c = world;
        this.b.setSeed(world.getSeed());
        long l = this.b.nextLong();
        long i1 = this.b.nextLong();
        long j1 = (long) (i >> 4) * l;
        long k1 = (long) (k >> 4) * i1;

        this.b.setSeed(j1 ^ k1 ^ world.getSeed());
        this.a(world, i >> 4, k >> 4, 0, 0, (byte[]) null);
        double d0 = Double.MAX_VALUE;
        ChunkPosition chunkposition = null;
        Iterator iterator = this.d.values().iterator();

        ChunkPosition chunkposition1;
        int l1;
        int i2;
        double d1;
        int j2;

        while (iterator.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator.next();

            if (structurestart.d()) {
                StructurePiece structurepiece = (StructurePiece) structurestart.b().get(0);

                chunkposition1 = structurepiece.a();
                i2 = chunkposition1.x - i;
                l1 = chunkposition1.y - j;
                j2 = chunkposition1.z - k;
                d1 = (double) (i2 + i2 * l1 * l1 + j2 * j2);
                if (d1 < d0) {
                    d0 = d1;
                    chunkposition = chunkposition1;
                }
            }
        }

        if (chunkposition != null) {
            return chunkposition;
        } else {
            List list = this.p_();

            if (list != null) {
                ChunkPosition chunkposition2 = null;
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext()) {
                    chunkposition1 = (ChunkPosition) iterator1.next();
                    i2 = chunkposition1.x - i;
                    l1 = chunkposition1.y - j;
                    j2 = chunkposition1.z - k;
                    d1 = (double) (i2 + i2 * l1 * l1 + j2 * j2);
                    if (d1 < d0) {
                        d0 = d1;
                        chunkposition2 = chunkposition1;
                    }
                }

                return chunkposition2;
            } else {
                return null;
            }
        }
    }

    protected List p_() {
        return null;
    }

    protected abstract boolean a(int i, int j);

    protected abstract StructureStart b(int i, int j);
}
