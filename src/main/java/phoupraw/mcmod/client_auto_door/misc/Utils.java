package phoupraw.mcmod.client_auto_door.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

import java.util.List;

public interface Utils {
    static List<Box> getAABBs(Entity entity) {
        List<Box> boxes = new ObjectArrayList<>();
        for (var i = entity; i != null; i = i.getVehicle()) {
            boxes.add(i.getBoundingBox());
        }
        return boxes;
    }
}
