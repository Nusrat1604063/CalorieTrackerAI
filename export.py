from ultralytics import YOLO
model = YOLO('food_detector_small.pt')  # Full path if needed, e.g., '/storage/emulated/0/Download/food_detector_small.pt'
model.export(format='tflite', imgsz=640, int8=True)
