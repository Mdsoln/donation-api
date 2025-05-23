import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:intl/intl.dart';
import 'package:open_file/open_file.dart';
import 'package:path_provider/path_provider.dart';
import 'dart:io';

class DonorReportScreen extends StatefulWidget {
  const DonorReportScreen({Key? key}) : super(key: key);

  @override
  _DonorReportScreenState createState() => _DonorReportScreenState();
}

class _DonorReportScreenState extends State<DonorReportScreen> {
  final _formKey = GlobalKey<FormState>();
  bool _isLoading = false;
  String? _error;
  Map<String, dynamic>? _reportData;

  // Form values
  String _reportType = 'QUARTERLY';
  String _reportFormat = 'JSON';
  int _year = DateTime.now().year;
  int _quarter = (DateTime.now().month - 1) ~/ 3 + 1;
  int _yearOnly = DateTime.now().year;
  DateTime? _startDate;
  DateTime? _endDate;
  bool _includeAppointmentStats = true;
  bool _includeRequestStats = true;
  bool _includeHospitalBreakdown = true;

  // Available options
  final List<int> _years = List.generate(
      6, (index) => DateTime.now().year - index);
  final List<int> _quarters = [1, 2, 3, 4];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Donor Report'),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            _buildReportForm(),
            if (_error != null) _buildErrorMessage(),
            if (_reportData != null) _buildReportResults(),
          ],
        ),
      ),
    );
  }

  Widget _buildReportForm() {
    return Card(
      elevation: 4,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'Generate Donation Report',
                style: TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 16),
              
              // Report Type
              DropdownButtonFormField<String>(
                decoration: const InputDecoration(
                  labelText: 'Report Type',
                  border: OutlineInputBorder(),
                ),
                value: _reportType,
                items: const [
                  DropdownMenuItem(value: 'QUARTERLY', child: Text('Quarterly')),
                  DropdownMenuItem(value: 'YEARLY', child: Text('Yearly')),
                  DropdownMenuItem(value: 'CUSTOM', child: Text('Custom Date Range')),
                  DropdownMenuItem(value: 'ALL_TIME', child: Text('All Time')),
                ],
                onChanged: (value) {
                  setState(() {
                    _reportType = value!;
                  });
                },
              ),
              const SizedBox(height: 16),
              
              // Report Format
              DropdownButtonFormField<String>(
                decoration: const InputDecoration(
                  labelText: 'Report Format',
                  border: OutlineInputBorder(),
                ),
                value: _reportFormat,
                items: const [
                  DropdownMenuItem(value: 'JSON', child: Text('JSON (View Online)')),
                  DropdownMenuItem(value: 'PDF', child: Text('PDF')),
                  DropdownMenuItem(value: 'EXCEL', child: Text('Excel')),
                ],
                onChanged: (value) {
                  setState(() {
                    _reportFormat = value!;
                  });
                },
              ),
              const SizedBox(height: 16),
              
              // Quarterly Options
              if (_reportType == 'QUARTERLY') ...[
                Row(
                  children: [
                    Expanded(
                      child: DropdownButtonFormField<int>(
                        decoration: const InputDecoration(
                          labelText: 'Year',
                          border: OutlineInputBorder(),
                        ),
                        value: _year,
                        items: _years.map((year) => DropdownMenuItem(
                          value: year,
                          child: Text(year.toString()),
                        )).toList(),
                        onChanged: (value) {
                          setState(() {
                            _year = value!;
                          });
                        },
                      ),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: DropdownButtonFormField<int>(
                        decoration: const InputDecoration(
                          labelText: 'Quarter',
                          border: OutlineInputBorder(),
                        ),
                        value: _quarter,
                        items: _quarters.map((quarter) => DropdownMenuItem(
                          value: quarter,
                          child: Text('Q$quarter'),
                        )).toList(),
                        onChanged: (value) {
                          setState(() {
                            _quarter = value!;
                          });
                        },
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
              ],
              
              // Yearly Options
              if (_reportType == 'YEARLY') ...[
                DropdownButtonFormField<int>(
                  decoration: const InputDecoration(
                    labelText: 'Year',
                    border: OutlineInputBorder(),
                  ),
                  value: _yearOnly,
                  items: _years.map((year) => DropdownMenuItem(
                    value: year,
                    child: Text(year.toString()),
                  )).toList(),
                  onChanged: (value) {
                    setState(() {
                      _yearOnly = value!;
                    });
                  },
                ),
                const SizedBox(height: 16),
              ],
              
              // Custom Date Range Options
              if (_reportType == 'CUSTOM') ...[
                Row(
                  children: [
                    Expanded(
                      child: TextFormField(
                        decoration: const InputDecoration(
                          labelText: 'Start Date',
                          border: OutlineInputBorder(),
                          suffixIcon: Icon(Icons.calendar_today),
                        ),
                        readOnly: true,
                        controller: TextEditingController(
                          text: _startDate != null 
                              ? DateFormat('yyyy-MM-dd').format(_startDate!) 
                              : '',
                        ),
                        onTap: () async {
                          final date = await showDatePicker(
                            context: context,
                            initialDate: _startDate ?? DateTime.now(),
                            firstDate: DateTime(2000),
                            lastDate: DateTime.now(),
                          );
                          if (date != null) {
                            setState(() {
                              _startDate = date;
                            });
                          }
                        },
                        validator: (value) {
                          if (_reportType == 'CUSTOM' && _startDate == null) {
                            return 'Please select a start date';
                          }
                          return null;
                        },
                      ),
                    ),
                    const SizedBox(width: 16),
                    Expanded(
                      child: TextFormField(
                        decoration: const InputDecoration(
                          labelText: 'End Date',
                          border: OutlineInputBorder(),
                          suffixIcon: Icon(Icons.calendar_today),
                        ),
                        readOnly: true,
                        controller: TextEditingController(
                          text: _endDate != null 
                              ? DateFormat('yyyy-MM-dd').format(_endDate!) 
                              : '',
                        ),
                        onTap: () async {
                          final date = await showDatePicker(
                            context: context,
                            initialDate: _endDate ?? DateTime.now(),
                            firstDate: DateTime(2000),
                            lastDate: DateTime.now(),
                          );
                          if (date != null) {
                            setState(() {
                              _endDate = date;
                            });
                          }
                        },
                        validator: (value) {
                          if (_reportType == 'CUSTOM' && _endDate == null) {
                            return 'Please select an end date';
                          }
                          return null;
                        },
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
              ],
              
              // Additional Options
              CheckboxListTile(
                title: const Text('Include Appointment Statistics'),
                value: _includeAppointmentStats,
                onChanged: (value) {
                  setState(() {
                    _includeAppointmentStats = value!;
                  });
                },
                controlAffinity: ListTileControlAffinity.leading,
              ),
              CheckboxListTile(
                title: const Text('Include Request Statistics'),
                value: _includeRequestStats,
                onChanged: (value) {
                  setState(() {
                    _includeRequestStats = value!;
                  });
                },
                controlAffinity: ListTileControlAffinity.leading,
              ),
              CheckboxListTile(
                title: const Text('Include Hospital Breakdown'),
                value: _includeHospitalBreakdown,
                onChanged: (value) {
                  setState(() {
                    _includeHospitalBreakdown = value!;
                  });
                },
                controlAffinity: ListTileControlAffinity.leading,
              ),
              const SizedBox(height: 16),
              
              // Buttons
              Row(
                children: [
                  Expanded(
                    child: ElevatedButton(
                      onPressed: _isLoading ? null : _generateReport,
                      child: _isLoading
                          ? const SizedBox(
                              height: 20,
                              width: 20,
                              child: CircularProgressIndicator(
                                strokeWidth: 2,
                              ),
                            )
                          : const Text('Generate Report'),
                    ),
                  ),
                  const SizedBox(width: 8),
                  ElevatedButton(
                    onPressed: _isLoading ? null : () => _exportReport('PDF'),
                    child: const Text('PDF'),
                  ),
                  const SizedBox(width: 8),
                  ElevatedButton(
                    onPressed: _isLoading ? null : () => _exportReport('EXCEL'),
                    child: const Text('Excel'),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildErrorMessage() {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 16.0),
      child: Card(
        color: Colors.red[100],
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Text(
            _error!,
            style: TextStyle(color: Colors.red[900]),
          ),
        ),
      ),
    );
  }

  Widget _buildReportResults() {
    return Card(
      elevation: 4,
      margin: const EdgeInsets.only(top: 16),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Report for ${_reportData!['donorName']}',
              style: const TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text('Period: ${_reportData!['reportPeriod']}'),
            const SizedBox(height: 16),
            
            // Donation Summary
            const Text(
              'Donation Summary',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            _buildInfoRow('Total Donations', '${_reportData!['totalDonations']}'),
            _buildInfoRow('Total Volume', '${_reportData!['totalVolumeMl'].toStringAsFixed(2)} ml'),
            if (_reportData!['firstDonationDate'] != null)
              _buildInfoRow('First Donation', _formatDate(_reportData!['firstDonationDate'])),
            if (_reportData!['lastDonationDate'] != null)
              _buildInfoRow('Last Donation', _formatDate(_reportData!['lastDonationDate'])),
            const SizedBox(height: 16),
            
            // Appointment Statistics
            if (_includeAppointmentStats) ...[
              const Text(
                'Appointment Statistics',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              _buildInfoRow('Total Appointments', '${_reportData!['totalAppointments']}'),
              _buildInfoRow('Completed', '${_reportData!['completedAppointments']}'),
              _buildInfoRow('Scheduled', '${_reportData!['scheduledAppointments']}'),
              _buildInfoRow('Expired', '${_reportData!['expiredAppointments']}'),
              _buildInfoRow('Cancelled', '${_reportData!['cancelledAppointments']}'),
              const SizedBox(height: 16),
            ],
            
            // Hospital Breakdown
            if (_includeHospitalBreakdown && 
                _reportData!['hospitalData'] != null && 
                (_reportData!['hospitalData'] as List).isNotEmpty) ...[
              const Text(
                'Hospital Breakdown',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              DataTable(
                columns: const [
                  DataColumn(label: Text('Hospital')),
                  DataColumn(label: Text('Donations')),
                  DataColumn(label: Text('Volume (ml)')),
                ],
                rows: (_reportData!['hospitalData'] as List).map<DataRow>((hospital) {
                  return DataRow(
                    cells: [
                      DataCell(Text(hospital['hospitalName'])),
                      DataCell(Text('${hospital['donations']}')),
                      DataCell(Text(hospital['volumeMl'].toStringAsFixed(2))),
                    ],
                  );
                }).toList(),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: Row(
        children: [
          Expanded(
            flex: 2,
            child: Text(label, style: const TextStyle(fontWeight: FontWeight.w500)),
          ),
          Expanded(
            flex: 3,
            child: Text(value),
          ),
        ],
      ),
    );
  }

  String _formatDate(String dateString) {
    final date = DateTime.parse(dateString);
    return DateFormat('dd MMM yyyy').format(date);
  }

  Future<void> _generateReport() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      // Get donor ID from user context or route parameter
      final donorId = 1; // Replace with actual donor ID
      
      final response = await http.post(
        Uri.parse('http://localhost:8080/api/reports/donor/$donorId'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer YOUR_AUTH_TOKEN', // Replace with actual token
        },
        body: jsonEncode(_prepareRequestBody()),
      );

      if (response.statusCode == 200) {
        setState(() {
          _reportData = jsonDecode(response.body);
        });
      } else {
        setState(() {
          _error = 'Error generating report: ${response.statusCode}';
        });
      }
    } catch (e) {
      setState(() {
        _error = 'Error generating report: $e';
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  Future<void> _exportReport(String format) async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      // Get donor ID from user context or route parameter
      final donorId = 1; // Replace with actual donor ID
      
      final requestBody = _prepareRequestBody();
      requestBody['reportFormat'] = format;
      
      final response = await http.post(
        Uri.parse('http://localhost:8080/api/reports/donor/$donorId/export/${format.toLowerCase()}'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer YOUR_AUTH_TOKEN', // Replace with actual token
        },
        body: jsonEncode(requestBody),
      );

      if (response.statusCode == 200) {
        // Save file and open it
        final directory = await getApplicationDocumentsDirectory();
        final extension = format.toLowerCase() == 'pdf' ? 'pdf' : 'xlsx';
        final file = File('${directory.path}/donor_report.$extension');
        await file.writeAsBytes(response.bodyBytes);
        
        // Open the file
        await OpenFile.open(file.path);
      } else {
        setState(() {
          _error = 'Error exporting report: ${response.statusCode}';
        });
      }
    } catch (e) {
      setState(() {
        _error = 'Error exporting report: $e';
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  Map<String, dynamic> _prepareRequestBody() {
    final requestBody = <String, dynamic>{
      'reportType': _reportType,
      'reportFormat': _reportFormat,
      'includeAppointmentStats': _includeAppointmentStats,
      'includeRequestStats': _includeRequestStats,
      'includeHospitalBreakdown': _includeHospitalBreakdown,
    };
    
    if (_reportType == 'QUARTERLY') {
      requestBody['year'] = _year;
      requestBody['quarter'] = _quarter;
    } else if (_reportType == 'YEARLY') {
      requestBody['yearOnly'] = _yearOnly;
    } else if (_reportType == 'CUSTOM') {
      requestBody['startDate'] = DateFormat('yyyy-MM-dd').format(_startDate!);
      requestBody['endDate'] = DateFormat('yyyy-MM-dd').format(_endDate!);
    }
    
    return requestBody;
  }
}