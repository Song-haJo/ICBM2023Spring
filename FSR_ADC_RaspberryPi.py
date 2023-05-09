import time
from Iot.Device.Adc import Adc
from force_sensitive_resistor import FsrWithAdcSample, FsrWithCapacitorSample

print("Hello Fsr408 capacitor Sample!")

# Use this sample when using ADC for reading          
StartReadingWithADC()

# Use this sample if using capacitor for reading
# StartReadingWithCapacitor()

def StartReadingWithADC():
    fsr_with_adc = FsrWithAdcSample()
    
    while True:
        value = fsr_with_adc.Read(0)
        voltage = fsr_with_adc.CalculateVoltage(value)
        resistance = fsr_with_adc.CalculateFsrResistance(voltage)
        force = fsr_with_adc.CalculateForce(resistance)
        print(f'Read value: {value}, milli voltage: {voltage:.2f}, resistance: {resistance:.2f}, approximate force in Newtons: {force:.2f}')
        time.sleep(0.5)

def StartReadingWithCapacitor():
    fsr_with_capacitor = FsrWithCapacitorSample()

    while True:
        value = fsr_with_capacitor.ReadCapacitorChargingDuration()

        if value == 30000:
            print("Not pressed")
        else:
            print(f"Pressed {value}")
        time.sleep(0.5)
